package edu.buffalo.distributedsystems.eventbrokerc.repository;

import edu.buffalo.distributedsystems.eventbrokerc.entity.TopicBrokerMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventBrokerRepository extends CrudRepository<TopicBrokerMapping, String> {
}
