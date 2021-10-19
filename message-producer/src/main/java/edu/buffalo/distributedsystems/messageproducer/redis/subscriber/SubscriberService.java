package edu.buffalo.distributedsystems.messageproducer.redis.subscriber;

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
public class SubscriberService {
    private final ReactiveRedisOperations<String, String> reactiveRedisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);

    @Value("${topic.name:website-health}")
    private String topic;

    @Autowired
    public SubscriberService(@Qualifier("messageTemplate") ReactiveRedisOperations<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @PostConstruct
    private void init() {
        logger.info("PostConstruct");
        this.reactiveRedisTemplate
                .listenTo(ChannelTopic.of(topic))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(logger::info);
    }
}
