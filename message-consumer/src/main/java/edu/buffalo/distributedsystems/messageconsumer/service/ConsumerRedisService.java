package edu.buffalo.distributedsystems.messageconsumer.service;

import edu.buffalo.distributedsystems.messageconsumer.model.Notifications;
import edu.buffalo.distributedsystems.messageconsumer.payload.Notification;
import edu.buffalo.distributedsystems.messageconsumer.payload.UserMessage;
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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ConsumerRedisService {
    private final ReactiveRedisOperations<String, ConsumerMessage> redisTemplate;

    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ConsumerRedisService.class);

    @Value("${topic.name:consumer-website-health}")
    private String consumerWebsiteHealthTopic;

    private NotificationsRepository repository;

    @Autowired
    public ConsumerRedisService(@Qualifier("consumerMessageTemplate") ReactiveRedisOperations<String, ConsumerMessage> redisTemplate, SimpMessagingTemplate messagingTemplate, NotificationService service, NotificationsRepository repository) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        this.redisTemplate
            .listenTo(ChannelTopic.of(consumerWebsiteHealthTopic))
            .map(ReactiveSubscription.Message::getMessage)
            .subscribe((eventMessage -> {
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
            }));
    }
}
