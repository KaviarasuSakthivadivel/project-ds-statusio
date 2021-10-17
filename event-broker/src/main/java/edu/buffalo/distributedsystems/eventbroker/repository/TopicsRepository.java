package edu.buffalo.distributedsystems.eventbroker.repository;

import edu.buffalo.distributedsystems.eventbroker.model.Topics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicsRepository extends JpaRepository<Topics, String> {
}
