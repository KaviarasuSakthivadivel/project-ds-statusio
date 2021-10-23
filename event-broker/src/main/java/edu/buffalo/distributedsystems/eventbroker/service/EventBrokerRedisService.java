package edu.buffalo.distributedsystems.eventbroker.service;

import edu.buffalo.distributedsystems.eventbroker.model.Consumers;
import edu.buffalo.distributedsystems.eventbroker.model.TopicSubscriptions;
import edu.buffalo.distributedsystems.eventbroker.model.Topics;
import edu.buffalo.distributedsystems.eventbroker.repository.ConsumerRepository;
import edu.buffalo.distributedsystems.eventbroker.repository.TopicSubscriptionRepository;
import edu.buffalo.distributedsystems.eventbroker.repository.TopicsRepository;
import edu.buffalo.distributedsystems.serviceutil.dto.ConsumerMessage;
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
import java.util.Set;

@Service
public class EventBrokerRedisService {
    private final ReactiveRedisOperations<String, EventMessage> redisTemplate;
    private final ReactiveRedisOperations<String, ConsumerMessage> consumerRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EventBrokerRedisService.class);

    @Value("${topic.name:website-health}")
    private String eventBrokerTopic;

    @Value("${topic.name:consumer-website-health}")
    private String consumerWebsiteHealthTopic;

    private final ConsumerRepository consumerRepository;
    private final TopicsRepository topicsRepository;
    private final TopicSubscriptionRepository subscriptionRepository;

    @Autowired
    public EventBrokerRedisService(@Qualifier("messageTemplate") ReactiveRedisOperations<String, EventMessage> redisTemplate, @Qualifier("consumerMessageTemplate") ReactiveRedisOperations<String, ConsumerMessage> consumerRedisTemplate, ConsumerRepository consumerRepository, TopicsRepository topicsRepository, TopicSubscriptionRepository subscriptionRepository) {
        this.redisTemplate = redisTemplate;
        this.consumerRedisTemplate = consumerRedisTemplate;
        this.consumerRepository = consumerRepository;
        this.topicsRepository = topicsRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    private void init() {
        this.redisTemplate
            .listenTo(ChannelTopic.of(eventBrokerTopic))
            .map(ReactiveSubscription.Message::getMessage)
            .subscribe((eventMessage -> {
                String eventName = eventMessage.getEventName();
                logger.info("Deciding on where to route the message event :: " + eventName);
                if(eventName.equals("website-health")) {
                    Topics currentTopic = this.topicsRepository.getByTopicName("website-health");

                    if(currentTopic != null) {
                        logger.info("--------------Processing message received for topic " + currentTopic.getTopicName() + " -----------------");
                        Set<TopicSubscriptions> topicSubscriptionsList = currentTopic.getConsumers();
                        for(TopicSubscriptions topic : topicSubscriptionsList) {
                            Consumers consumers = topic.getConsumers();
                            logger.info("-------- Sending message event to consumer " + consumers.getEmailId() + " -----------");

                            ConsumerMessage consumerMessage = new ConsumerMessage();
                            consumerMessage.setCreatedOn(eventMessage.getCreatedOn());
                            consumerMessage.setEmailId(consumers.getEmailId());
                            consumerMessage.setUserId(consumers.getConsumer_id());
                            consumerMessage.setStatus(eventMessage.getStatus());
                            consumerMessage.setWebsiteId(eventMessage.getWebsiteId());
                            consumerMessage.setWebsiteUrl(eventMessage.getWebsiteUrl());
                            consumerMessage.setWebsiteName(eventMessage.getWebsiteName());

                            this.consumerRedisTemplate.convertAndSend(consumerWebsiteHealthTopic, consumerMessage).subscribe();
                            logger.info("-------- Sent message event to consumer " + consumers.getEmailId() + " -----------");
                        }
                    }
                }
            }));
    }
}
