package edu.buffalo.distributedsystems.eventbroker.repository;

import edu.buffalo.distributedsystems.eventbroker.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumers, String> {
}
