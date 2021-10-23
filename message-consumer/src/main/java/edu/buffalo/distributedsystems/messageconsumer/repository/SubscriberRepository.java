package edu.buffalo.distributedsystems.messageconsumer.repository;

import edu.buffalo.distributedsystems.messageconsumer.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {}
