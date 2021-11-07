package edu.buffalo.distributedsystems.eventbroker.service;

import edu.buffalo.distributedsystems.eventbroker.entity.TopicBrokerMapping;
import edu.buffalo.distributedsystems.eventbroker.model.Consumers;
import edu.buffalo.distributedsystems.eventbroker.model.TopicSubscriptions;
import edu.buffalo.distributedsystems.eventbroker.model.Topics;
import edu.buffalo.distributedsystems.eventbroker.repository.ConsumerRepository;
import edu.buffalo.distributedsystems.eventbroker.repository.EventBrokerRepository;
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
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
public class EventBrokerRedisService {
    private final ReactiveRedisOperations<String, EventMessage> redisTemplate;
    private final ReactiveRedisOperations<String, ConsumerMessage> consumerRedisTemplate;
    private final static String INTERNAL_DOMAIN_HEALTH = "internal-domain-health";
    private final static String EXTERNAL_DOMAIN_HEALTH = "external-domain-health";
    private final static String THIRD_PARTY_DOMAIN_HEALTH = "tp-domain-health";

    private static final Logger logger = LoggerFactory.getLogger(EventBrokerRedisService.class);

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-e}")
    private String BROKER_INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-e}")
    private String BROKER_EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-e}")
    private String BROKER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:consumer-website-health}")
    private String consumerWebsiteHealthTopic;

    private final ConsumerRepository consumerRepository;
    private final TopicsRepository topicsRepository;
    private final TopicSubscriptionRepository subscriptionRepository;
    @Autowired
    private EventBrokerRepository eventBrokerRepository;

    @Autowired
    public EventBrokerRedisService(@Qualifier("messageTemplate") ReactiveRedisOperations<String, EventMessage> redisTemplate, @Qualifier("consumerMessageTemplate") ReactiveRedisOperations<String, ConsumerMessage> consumerRedisTemplate, ConsumerRepository consumerRepository, TopicsRepository topicsRepository, TopicSubscriptionRepository subscriptionRepository) {
        this.redisTemplate = redisTemplate;
        this.consumerRedisTemplate = consumerRedisTemplate;
        this.consumerRepository = consumerRepository;
        this.topicsRepository = topicsRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    private void processEventMessage(EventMessage eventMessage) {
        String eventName = eventMessage.getEventName();
        logger.info("Deciding on where to route the message event :: " + eventName);

        try {
            InetAddress ip = InetAddress.getLocalHost();
            logger.info(Arrays.toString(ip.getAddress()));
            logger.info("------------- Connecting to IP address :: " + ip + " Host Name :: " + ip.getHostName() + " -------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(eventName.equals(INTERNAL_DOMAIN_HEALTH) || eventName.equals(EXTERNAL_DOMAIN_HEALTH) || eventName.equals(THIRD_PARTY_DOMAIN_HEALTH)) {
            Topics currentTopic = this.topicsRepository.getByTopicName(eventName);

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

                    switch (eventName) {
                        case INTERNAL_DOMAIN_HEALTH:
                            this.consumerRedisTemplate.convertAndSend(CONSUMER_INTERNAL_DOMAIN_HEALTH_TOPIC, consumerMessage).subscribe();
                            break;
                        case EXTERNAL_DOMAIN_HEALTH:
                            this.consumerRedisTemplate.convertAndSend(CONSUMER_EXTERNAL_DOMAIN_HEALTH_TOPIC, consumerMessage).subscribe();
                            break;
                        case THIRD_PARTY_DOMAIN_HEALTH:
                            this.consumerRedisTemplate.convertAndSend(CONSUMER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC, consumerMessage).subscribe();
                            break;
                    }
                    logger.info("-------- Sent message event to consumer " + consumers.getEmailId() + " -----------");
                }
            }
        }
    }

    @PostConstruct
    private void init() {
        this.redisTemplate
            .listenTo(ChannelTopic.of(BROKER_INTERNAL_DOMAIN_HEALTH_TOPIC))
            .map(ReactiveSubscription.Message::getMessage)
            .subscribe((this::processEventMessage));

        this.redisTemplate
                .listenTo(ChannelTopic.of(BROKER_EXTERNAL_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((this::processEventMessage));

        this.redisTemplate
                .listenTo(ChannelTopic.of(BROKER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe((this::processEventMessage));

        try {
            InetAddress ip = InetAddress.getLocalHost();
            logger.info(Arrays.toString(ip.getAddress()));
            logger.info("------------- Registering the to IP address :: " + ip + " Host Name :: " + ip.getHostName() + " -------------------");

            final TopicBrokerMapping mapping = new TopicBrokerMapping(ip.getHostAddress(), "internal-domain-health");
            eventBrokerRepository.save(mapping);

            if(eventBrokerRepository.findById(mapping.getId()).isPresent()) {
                final TopicBrokerMapping retrievedMapping = eventBrokerRepository.findById(mapping.getId()).get();
                logger.info("retrived mapping " +  retrievedMapping.getTopicName() + " => " + retrievedMapping.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
