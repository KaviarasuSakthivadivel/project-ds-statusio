package edu.buffalo.distributedsystems.eventbrokerb.repository;

import edu.buffalo.distributedsystems.eventbrokerb.model.Topics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicsRepository extends JpaRepository<Topics, String> {
    Topics getByTopicName(String topicName);
}
