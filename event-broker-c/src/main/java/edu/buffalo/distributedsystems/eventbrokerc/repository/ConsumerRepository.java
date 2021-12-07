package edu.buffalo.distributedsystems.eventbrokerc.repository;

import edu.buffalo.distributedsystems.eventbrokerc.model.Consumers;
import edu.buffalo.distributedsystems.eventbrokerc.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumers, String> {
}
