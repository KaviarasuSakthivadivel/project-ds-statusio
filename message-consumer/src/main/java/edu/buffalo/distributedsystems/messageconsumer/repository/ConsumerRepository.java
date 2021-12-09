package edu.buffalo.distributedsystems.messageconsumer.repository;

import edu.buffalo.distributedsystems.messageconsumer.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumers, String> {
}
