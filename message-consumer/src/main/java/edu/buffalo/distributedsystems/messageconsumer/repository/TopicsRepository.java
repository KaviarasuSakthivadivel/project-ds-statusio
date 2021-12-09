package edu.buffalo.distributedsystems.messageconsumer.repository;

import edu.buffalo.distributedsystems.messageconsumer.model.Topics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicsRepository extends JpaRepository<Topics, String> {
    Topics getByTopicName(String topicName);
}
