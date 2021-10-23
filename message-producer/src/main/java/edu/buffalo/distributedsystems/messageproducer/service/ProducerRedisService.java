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

    @Value("${topic.name:website-health-notify}")
    private String topic;

    @Value("${topic.name:website-health}")
    private String eventBrokerTopic;

    @Autowired
    public ProducerRedisService(@Qualifier("messageTemplate") ReactiveRedisOperations<String, EventMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Message is getting produced / published from here.
    @PostConstruct
    private void init() {
        logger.info("PostConstruct");
        this.redisTemplate
                .listenTo(ChannelTopic.of(topic))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((eventMessage -> {
                    logger.info("Event message :: "  + eventMessage.getEventName() + " " + eventMessage.getWebsiteUrl());
                    this.redisTemplate.convertAndSend(eventBrokerTopic, eventMessage).subscribe();
                }));
    }
}
