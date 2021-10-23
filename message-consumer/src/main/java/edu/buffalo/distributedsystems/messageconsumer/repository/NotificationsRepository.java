package edu.buffalo.distributedsystems.messageconsumer.repository;

import edu.buffalo.distributedsystems.messageconsumer.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications, String> {

}
