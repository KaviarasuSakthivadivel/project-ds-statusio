package edu.buffalo.distributedsystems.messageproducer.service;

import edu.buffalo.distributedsystems.serviceutil.dto.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ProducerRedisService {
    private final ReactiveRedisOperations<String, EventMessage> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProducerRedisService.class);
    private final static String INTERNAL_DOMAIN_HEALTH = "internal-domain-health";
    private final static String EXTERNAL_DOMAIN_HEALTH = "external-domain-health";
    private final static String THIRD_PARTY_DOMAIN_HEALTH = "tp-domain-health";

    @Value("${topic.name:internal-domain-health-p}")
    private String topic;

    @Value("${topic.name:internal-domain-health-e}")
    private String eventBrokerTopic;

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-p}")
    private String PRODUCER_INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-p}")
    private String PRODUCER_EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-p}")
    private String PRODUCER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-e}")
    private String BROKER_INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-e}")
    private String BROKER_EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-e}")
    private String BROKER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    @Autowired
    public ProducerRedisService(@Qualifier("messageTemplate") ReactiveRedisOperations<String, EventMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Message is getting produced / published from here.
    @PostConstruct
    private void init() {
        logger.info("PostConstruct");
        this.redisTemplate
                .listenTo(ChannelTopic.of(PRODUCER_INTERNAL_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((eventMessage -> {
                    logger.info("Event message :: "  + eventMessage.getEventName() + " " + eventMessage.getWebsiteUrl());
                    this.redisTemplate.convertAndSend(BROKER_INTERNAL_DOMAIN_HEALTH_TOPIC, eventMessage).subscribe();
                }));

        this.redisTemplate
                .listenTo(ChannelTopic.of(PRODUCER_EXTERNAL_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((eventMessage -> {
                    logger.info("Event message :: "  + eventMessage.getEventName() + " " + eventMessage.getWebsiteUrl());
                    this.redisTemplate.convertAndSend(BROKER_EXTERNAL_DOMAIN_HEALTH_TOPIC, eventMessage).subscribe();
                }));

        this.redisTemplate
                .listenTo(ChannelTopic.of(PRODUCER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((eventMessage -> {
                    logger.info("Event message :: "  + eventMessage.getEventName() + " " + eventMessage.getWebsiteUrl());
                    this.redisTemplate.convertAndSend(BROKER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC, eventMessage).subscribe();
                }));
    }
}
