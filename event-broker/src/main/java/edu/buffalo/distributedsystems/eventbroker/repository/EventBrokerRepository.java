package edu.buffalo.distributedsystems.eventbroker.repository;

import edu.buffalo.distributedsystems.eventbroker.entity.TopicBrokerMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventBrokerRepository extends CrudRepository<TopicBrokerMapping, String> {
}
