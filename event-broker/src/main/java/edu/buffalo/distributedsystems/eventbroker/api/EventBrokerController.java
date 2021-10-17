package edu.buffalo.distributedsystems.eventbroker.api;

import edu.buffalo.distributedsystems.eventbroker.model.Consumers;
import edu.buffalo.distributedsystems.eventbroker.model.Topics;
import edu.buffalo.distributedsystems.eventbroker.payload.Topic;
import edu.buffalo.distributedsystems.eventbroker.repository.ConsumerRepository;
import edu.buffalo.distributedsystems.eventbroker.repository.TopicsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/broker")
public class EventBrokerController {
    private final Logger logger = LoggerFactory.getLogger(EventBrokerController.class);

    private final ConsumerRepository consumerRepository;
    private final TopicsRepository topicsRepository;

    @Autowired
    public EventBrokerController(ConsumerRepository consumerRepository, TopicsRepository topicsRepository) {
        this.consumerRepository = consumerRepository;
        this.topicsRepository = topicsRepository;
    }

    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Topics>> getAllTopics() {
        List<Topics> allTopics = this.topicsRepository.findAll();
        logger.info("Number of Topics: " + allTopics.size());
        return new ResponseEntity<>(allTopics, HttpStatus.OK);
    }

    @PostMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Topics> createConsumer(@RequestBody Topic topic) {
        logger.debug("Adding Topic into db :: topic name :: " + topic.getTopic_name());
        Topics topicRow = new Topics();
        topicRow.setTopic_name(topic.getTopic_name());
        topicRow.setTopic_id(UUID.randomUUID().toString());
        this.topicsRepository.save(topicRow);
        return new ResponseEntity<>(topicRow, HttpStatus.OK);
    }

    @GetMapping(value = "/consumers", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Consumers>> getAllConsumers() {
        List<Consumers> allTopics = this.consumerRepository.findAll();
        logger.info("Number of Consumers: " + allTopics.size());
        return new ResponseEntity<>(allTopics, HttpStatus.OK);
    }

    @GetMapping(value = "/consumers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Consumers> getConsumer(@PathVariable String id) {
        Optional<Consumers> consumer = Optional.of(this.consumerRepository.getById(id));
        if(!consumer.isPresent()) {
            logger.info("Consumers: " + consumer.toString());
            return new ResponseEntity<>(consumer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
