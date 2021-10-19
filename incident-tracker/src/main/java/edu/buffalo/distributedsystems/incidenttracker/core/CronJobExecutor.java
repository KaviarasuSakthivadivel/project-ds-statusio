package edu.buffalo.distributedsystems.incidenttracker.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteHealth;
import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteMonitor;
import edu.buffalo.distributedsystems.incidenttracker.repository.WebsiteHealthRepository;
import edu.buffalo.distributedsystems.incidenttracker.repository.WebsiteMonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CronJobExecutor {

    private final Logger logger = LoggerFactory.getLogger(CronJobExecutor.class);
    private final WebsiteMonitorRepository repository;
    private final WebsiteHealthRepository healthRepository;
    private final ReactiveRedisOperations<String, String> redisTemplate;

    @Autowired
    public CronJobExecutor(WebsiteMonitorRepository repository, WebsiteHealthRepository healthRepository, @Qualifier("messageTemplate") ReactiveRedisOperations<String, String> redisTemplate) {
        this.repository = repository;
        this.healthRepository = healthRepository;
        this.redisTemplate = redisTemplate;
    }

    @Value("${topic.name:website-health}")
    private String topic;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleFixedDelayTask() {
        logger.info("Executing CRON JOB for tracking the website details - " + new Date());
        List<WebsiteMonitor> websiteMonitors = this.repository.findAll();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        for(WebsiteMonitor monitor: websiteMonitors) {
            WebsiteHealth health = checkWebsiteHealth(monitor);
            String monitorJSONStr = gson.toJson(monitor);
            Map<String, Object> postRequest = new HashMap<>();
            postRequest.put("eventName", "website-health");
            postRequest.put("message", monitorJSONStr);
//            String response = DoHTTPRequest.doPost(postRequest);

//            EventMessage eventMessage = new EventMessage();
//            eventMessage.setMessage(monitorJSONStr);
//            eventMessage.setEventName("website-health");
            this.redisTemplate.convertAndSend(topic, "Hello world").subscribe();
            logger.info("Message produced :: ");
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
            logger.info("STATUS of :: " + monitor.getWebsite_url() + " ---> " + responseCode);
            health.setResponse_code(responseCode);
            if(responseCode == HttpStatus.OK.value()) {
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
