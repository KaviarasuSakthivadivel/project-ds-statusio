package edu.buffalo.distributedsystems.incidenttracker.core;

import edu.buffalo.distributedsystems.incidenttracker.kafka.core.MessageProducer;
import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteHealth;
import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteMonitor;
import edu.buffalo.distributedsystems.incidenttracker.repository.WebsiteHealthRepository;
import edu.buffalo.distributedsystems.incidenttracker.repository.WebsiteMonitorRepository;
import edu.buffalo.distributedsystems.serviceutil.dto.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CronJobExecutor {
    private final static String INTERNAL_DOMAIN_HEALTH = "internal-domain-health";
    private final static String EXTERNAL_DOMAIN_HEALTH = "external-domain-health";
    private final static String THIRD_PARTY_DOMAIN_HEALTH = "tp-domain-health";
    private final Logger logger = LoggerFactory.getLogger(CronJobExecutor.class);
    private final WebsiteMonitorRepository repository;
    private final WebsiteHealthRepository healthRepository;
    private final ReactiveRedisOperations<String, EventMessage> redisTemplate;
    private final MessageProducer producer;

    @Autowired
    public CronJobExecutor(WebsiteMonitorRepository repository, WebsiteHealthRepository healthRepository, @Qualifier("messageTemplate") ReactiveRedisOperations<String, EventMessage> redisTemplate, MessageProducer producer) {
        this.repository = repository;
        this.healthRepository = healthRepository;
        this.redisTemplate = redisTemplate;
        this.producer = producer;
    }

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-p}")
    private String INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-p}")
    private String EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-p}")
    private String THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    @Value(value = "${internal.topic.name}")
    private String internalDomainHealthTopic;

    @Value(value = "${external.topic.name}")
    private String externalDomainHealthTopic;

    @Value(value = "${tp.topic.name}")
    private String tpDomainHealthTopic;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleFixedDelayTask() {
        logger.info("Executing CRON JOB for tracking the website details - " + new Date());
        List<WebsiteMonitor> websiteMonitors = this.repository.findAll();

        for(WebsiteMonitor monitor: websiteMonitors) {
            WebsiteHealth health = checkWebsiteHealth(monitor);
//            String monitorJSONStr = gson.toJson(monitor);
            EventMessage eventMessage = new EventMessage();
            eventMessage.setWebsiteName(monitor.getName());
            eventMessage.setWebsiteId(monitor.getId());
            eventMessage.setWebsiteUrl(monitor.getWebsite_url());
            eventMessage.setStatus(health.getStatus());
            eventMessage.setCreatedOn(health.getTracked_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            String eventMessageStr = eventMessage.getWebsiteName() + "#" + eventMessage.getWebsiteId() + "#" + eventMessage.getWebsiteUrl() + "#" + eventMessage.getStatus() + "#" + eventMessage.getCreatedOn();

            switch (monitor.getCategory()) {
                case INTERNAL_DOMAIN_HEALTH:
                    eventMessage.setEventName(INTERNAL_DOMAIN_HEALTH);
                    producer.sendMessage(eventMessageStr, internalDomainHealthTopic);
                    break;
                case EXTERNAL_DOMAIN_HEALTH:
                    eventMessage.setEventName(EXTERNAL_DOMAIN_HEALTH);
                    producer.sendMessage(eventMessageStr, externalDomainHealthTopic);
                    break;
                case THIRD_PARTY_DOMAIN_HEALTH:
                    eventMessage.setEventName(THIRD_PARTY_DOMAIN_HEALTH);
                    producer.sendMessage(eventMessageStr, tpDomainHealthTopic);
                    break;
            }
            logger.info("Message produced for topic :: "
                    + eventMessage.getEventName() + " for website details :: "
                    + eventMessage.getWebsiteUrl() + " : status :: " + eventMessage.getStatus()
                    + " on " + eventMessage.getCreatedOn());
        }
    }

    private WebsiteHealth checkWebsiteHealth(WebsiteMonitor monitor) {
        WebsiteHealth health = new WebsiteHealth();
        health.setId(UUID.randomUUID().toString());
        health.setWebsite_id(monitor.getId());
        health.setTracked_at(LocalDateTime.now());
        try {
            URL url = new URL(monitor.getWebsite_url());
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setInstanceFollowRedirects(false);
            int responseCode = huc.getResponseCode();
            logger.info("Status of :: " + monitor.getWebsite_url() + " ---> " + responseCode);
            health.setResponse_code(responseCode);
            if (responseCode == HttpStatus.OK.value()) {
                health.setStatus(0);
            } else {
                health.setStatus(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            health.setStatus(1);
            health.setResponse_code(HttpStatus.SERVICE_UNAVAILABLE.value());
        } finally {
            this.healthRepository.save(health);
        }

        return health;
    }
}
