package edu.buffalo.distributedsystems.statusio.repository;

import edu.buffalo.distributedsystems.statusio.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {}
