package edu.buffalo.distributedsystems.eventbrokerc.repository;

import edu.buffalo.distributedsystems.eventbrokerc.model.Topics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicsRepository extends JpaRepository<Topics, String> {
    Topics getByTopicName(String topicName);
}
