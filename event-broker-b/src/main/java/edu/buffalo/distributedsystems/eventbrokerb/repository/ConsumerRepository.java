package edu.buffalo.distributedsystems.eventbrokerb.repository;

import edu.buffalo.distributedsystems.eventbrokerb.model.Consumers;
import edu.buffalo.distributedsystems.eventbrokerb.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumers, String> {
}
