package edu.buffalo.distributedsystems.messageconsumer.service;

import edu.buffalo.distributedsystems.messageconsumer.model.Notifications;
import edu.buffalo.distributedsystems.messageconsumer.payload.Notification;
import edu.buffalo.distributedsystems.messageconsumer.repository.NotificationsRepository;
import edu.buffalo.distributedsystems.serviceutil.dto.ConsumerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ConsumerRedisService {
    private final ReactiveRedisOperations<String, ConsumerMessage> redisTemplate;
    private final static String INTERNAL_DOMAIN_HEALTH = "internal-domain-health";
    private final static String EXTERNAL_DOMAIN_HEALTH = "external-domain-health";
    private final static String THIRD_PARTY_DOMAIN_HEALTH = "tp-domain-health";

    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ConsumerRedisService.class);

    @Value("${topic.name:" + INTERNAL_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_INTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + EXTERNAL_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_EXTERNAL_DOMAIN_HEALTH_TOPIC;

    @Value("${topic.name:" + THIRD_PARTY_DOMAIN_HEALTH + "-c}")
    private String CONSUMER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC;

    private final NotificationsRepository repository;

    @Autowired
    public ConsumerRedisService(@Qualifier("consumerMessageTemplate") ReactiveRedisOperations<String, ConsumerMessage> redisTemplate, SimpMessagingTemplate messagingTemplate, NotificationService service, NotificationsRepository repository) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
    }

    private void processMessageEvent(ConsumerMessage eventMessage) {

        String websiteName = eventMessage.getWebsiteName();
        logger.info("Event message received for User :: " + websiteName);

        Notification notification = new Notification(websiteName);
        notification.setDate(eventMessage.getCreatedOn());
        notification.setStatus(String.valueOf(eventMessage.getStatus()));
        notification.setEmail(eventMessage.getEmailId());
        notification.setWebsiteName(eventMessage.getWebsiteName());
        notification.setWebsiteUrl(eventMessage.getWebsiteUrl());

        Notifications notificationRow = new Notifications();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(eventMessage.getCreatedOn(), formatter);
        notificationRow.setDate(time);
        notificationRow.setId(UUID.randomUUID().toString());
        notificationRow.setStatus(String.valueOf(eventMessage.getStatus()));
        notificationRow.setUser_id(eventMessage.getUserId());
        notificationRow.setWebsite_name(eventMessage.getWebsiteName());
        notificationRow.setEmail(eventMessage.getEmailId());
        notificationRow.setWebsite_url(eventMessage.getWebsiteUrl());
        this.repository.save(notificationRow);

        logger.info("Sending the notification to the user :: " + eventMessage.getEmailId());
        this.messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    @PostConstruct
    private void init() {
        this.redisTemplate
            .listenTo(ChannelTopic.of(CONSUMER_INTERNAL_DOMAIN_HEALTH_TOPIC))
            .map(ReactiveSubscription.Message::getMessage)
            .subscribe(this::processMessageEvent);

        this.redisTemplate
                .listenTo(ChannelTopic.of(CONSUMER_EXTERNAL_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(this::processMessageEvent);

        this.redisTemplate
                .listenTo(ChannelTopic.of(CONSUMER_THIRD_PARTY_DOMAIN_HEALTH_TOPIC))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(this::processMessageEvent);
    }
}
