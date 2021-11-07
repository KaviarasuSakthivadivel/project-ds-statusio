package edu.buffalo.distributedsystems.eventbrokerb.repository;

import edu.buffalo.distributedsystems.eventbrokerb.entity.TopicBrokerMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventBrokerRepository extends CrudRepository<TopicBrokerMapping, String> {
}
