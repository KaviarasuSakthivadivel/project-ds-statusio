package edu.buffalo.distributedsystems.messageconsumer.kafka;

import edu.buffalo.distributedsystems.messageconsumer.model.Consumers;
import edu.buffalo.distributedsystems.messageconsumer.model.Notifications;
import edu.buffalo.distributedsystems.messageconsumer.model.TopicSubscriptions;
import edu.buffalo.distributedsystems.messageconsumer.model.Topics;
import edu.buffalo.distributedsystems.messageconsumer.payload.Notification;
import edu.buffalo.distributedsystems.messageconsumer.repository.NotificationsRepository;
import edu.buffalo.distributedsystems.messageconsumer.repository.TopicsRepository;
import edu.buffalo.distributedsystems.serviceutil.dto.ConsumerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Service
public class KafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final String INTERNAL_DOMAIN_HEALTH = "internal-domain-health";
    private static final String EXTERNAL_DOMAIN_HEALTH = "external-domain-health";
    private static final String THIRD_PARTY_DOMAIN_HEALTH = "tp-domain-health";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationsRepository repository;
    private final TopicsRepository topicsRepository;

    public KafkaConsumer(SimpMessagingTemplate messagingTemplate, NotificationsRepository repository, TopicsRepository topicsRepository) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
        this.topicsRepository = topicsRepository;
    }

    @KafkaListener(topics = THIRD_PARTY_DOMAIN_HEALTH, groupId = "1")
    public void consumeTpDomainHealthMessages(String message) {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        incomingMessageEvent(THIRD_PARTY_DOMAIN_HEALTH, message);
    }

    @KafkaListener(topics = INTERNAL_DOMAIN_HEALTH, groupId = "1")
    public void consumeInternalDomainHealthMessages(String message) {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        incomingMessageEvent(INTERNAL_DOMAIN_HEALTH, message);
    }

    @KafkaListener(topics = EXTERNAL_DOMAIN_HEALTH, groupId = "1")
    public void consumeExternalDomainHealthMessages(String message) {
        logger.info(String.format("#### -> Consumed message -> %s", message));
        incomingMessageEvent(EXTERNAL_DOMAIN_HEALTH, message);
    }

    private void incomingMessageEvent(String eventName, String message) {
        String[] messages = message.split("#");
        logger.info(Arrays.toString(messages));
        ConsumerMessage consumerMessage = new ConsumerMessage();
        consumerMessage.setWebsiteName(messages[0]);
        consumerMessage.setWebsiteId(messages[1]);
        consumerMessage.setWebsiteUrl(messages[2]);
        consumerMessage.setStatus(Integer.parseInt(messages[3]));
        consumerMessage.setCreatedOn(messages[4]);
        processMessageEvent(eventName, consumerMessage);
    }

    private void processMessageEvent(String eventName, ConsumerMessage eventMessage) {
        if(eventName.equals(INTERNAL_DOMAIN_HEALTH) || eventName.equals(EXTERNAL_DOMAIN_HEALTH) || eventName.equals(THIRD_PARTY_DOMAIN_HEALTH)) {
            Topics currentTopic = this.topicsRepository.getByTopicName(eventName);

            if(currentTopic != null) {
                logger.info("--------------Processing message received for topic " + currentTopic.getTopicName() + " -----------------");
                Set<TopicSubscriptions> topicSubscriptionsList = currentTopic.getConsumers();
                for(TopicSubscriptions topic : topicSubscriptionsList) {
                    Consumers consumers = topic.getConsumers();
                    logger.info("-------- Sending message event to consumer " + consumers.getEmailId() + " -----------");
                    String websiteName = eventMessage.getWebsiteName();
                    logger.info("Event message received for User :: " + websiteName);
                    eventMessage.setUserId(consumers.getConsumer_id());
                    eventMessage.setEmailId(consumers.getEmailId());

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

                    logger.info("-------- Sent message event to consumer " + consumers.getEmailId() + " -----------");
                }
            }
        }



    }
}
