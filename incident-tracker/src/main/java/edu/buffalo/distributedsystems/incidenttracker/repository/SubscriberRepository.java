package edu.buffalo.distributedsystems.incidenttracker.repository;

import edu.buffalo.distributedsystems.incidenttracker.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {}
