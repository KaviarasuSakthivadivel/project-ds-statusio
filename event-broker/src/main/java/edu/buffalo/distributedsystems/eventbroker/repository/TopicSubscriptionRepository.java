package edu.buffalo.distributedsystems.eventbroker.repository;

import edu.buffalo.distributedsystems.eventbroker.model.TopicSubscriptions;
import edu.buffalo.distributedsystems.eventbroker.model.Topics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicSubscriptionRepository extends JpaRepository<TopicSubscriptions, String> {
    List<TopicSubscriptions> findByTopics(Topics topic);

    TopicSubscriptions findTopicSubscriptionsByConsumersAndAndTopics(String consumerId, String topicsId);

    void deleteTopicSubscriptionsByConsumersAndTopics(String consumerId, String topicsId);
}
