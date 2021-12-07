package edu.buffalo.distributedsystems.eventbrokerc.api;

import edu.buffalo.distributedsystems.eventbrokerc.model.Consumers;
import edu.buffalo.distributedsystems.eventbrokerc.model.TopicSubscriptions;
import edu.buffalo.distributedsystems.eventbrokerc.model.Topics;
import edu.buffalo.distributedsystems.eventbrokerc.payload.Consumer;
import edu.buffalo.distributedsystems.eventbrokerc.payload.ProducerPayload;
import edu.buffalo.distributedsystems.eventbrokerc.payload.Topic;
import edu.buffalo.distributedsystems.eventbrokerc.repository.ConsumerRepository;
import edu.buffalo.distributedsystems.eventbrokerc.repository.TopicSubscriptionRepository;
import edu.buffalo.distributedsystems.eventbrokerc.repository.TopicsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.*;

@RestController
@RequestMapping(path = "/broker")
public class EventBrokerController {
    private final Logger logger = LoggerFactory.getLogger(EventBrokerController.class);

    private final ConsumerRepository consumerRepository;
    private final TopicsRepository topicsRepository;
    private final TopicSubscriptionRepository subscriptionRepository;

    @Autowired
    public EventBrokerController(ConsumerRepository consumerRepository, TopicsRepository topicsRepository, TopicSubscriptionRepository subscriptionRepository) {
        this.consumerRepository = consumerRepository;
        this.topicsRepository = topicsRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Topics>> getAllTopics() {
        List<Topics> allTopics = this.topicsRepository.findAll();
        logger.info("Number of Topics: " + allTopics.size());
        return new ResponseEntity<>(allTopics, HttpStatus.OK);
    }

    @PostMapping(value = "/topics", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Topics> createTopic(@RequestBody Topic topic) {
        logger.debug("Adding Topic into db :: topic name :: " + topic.getTopic_name());
        Topics topicRow = new Topics();
        topicRow.setTopicName(topic.getTopic_name());
        topicRow.setTopicId(UUID.randomUUID().toString());
        this.topicsRepository.save(topicRow);
        return new ResponseEntity<>(topicRow, HttpStatus.OK);
    }

    @GetMapping(value = "/topics/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Topics> getTopic(@PathVariable String id) {
        Topics topic = this.topicsRepository.getByTopicName(id);
        if(topic != null) {
            return new ResponseEntity<>(topic, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/topics/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Topics> deleteTopic(@PathVariable String id) {
        Topics topic = this.topicsRepository.getByTopicName(id);
        if(topic != null) {
            this.topicsRepository.delete(topic);
            return new ResponseEntity<>(topic, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/consumers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Consumers> createConsumer(@RequestBody Consumer consumer) {
        logger.info("Adding Consumers into db :: consumer email id :: " + consumer.getEmail());
        Consumers consumerRow = new Consumers();
        consumerRow.setConsumer_id(UUID.randomUUID().toString());
        consumerRow.setActive(true);
        consumerRow.setFirstName(consumer.getFirst_name());
        consumerRow.setLast_name(consumer.getLast_name());
        consumerRow.setEmailId(consumer.getEmail());
        this.consumerRepository.save(consumerRow);
        return new ResponseEntity<>(consumerRow, HttpStatus.OK);
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
        return new ResponseEntity<>(consumer.get(), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/consumers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Consumers> deleteConsumer(@PathVariable String id) {
        Optional<Consumers> consumer = this.consumerRepository.findById(id);
        if(consumer.isPresent()) {
            this.consumerRepository.delete(consumer.get());
            return new ResponseEntity<>(consumer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/consumers/{id}/subscribe", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Consumers> subscribeToTopic(@PathVariable String id, @RequestParam String topicName) {
        logger.debug("Subscribing consumer to the topic :: topic name :: " + topicName);
        Optional<Topics> topic = Optional.ofNullable(this.topicsRepository.getByTopicName(topicName));
        if(!topic.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Consumers> consumer = this.consumerRepository.findById(id);
        if(consumer.isPresent()) {
            TopicSubscriptions subscription = new TopicSubscriptions();
            subscription.setTopic_subscription_id(UUID.randomUUID().toString());
            subscription.setConsumers(consumer.get());
            subscription.setTopics(topic.get());
            this.subscriptionRepository.save(subscription);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/consumers/{id}/subscribe", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Consumers> unsubscribeFromTopic(@PathVariable String id, @RequestParam String topicName) {
        logger.debug("Unsubscribing consumer from the topic :: topic name :: " + topicName);
        Optional<Topics> topic = Optional.ofNullable(this.topicsRepository.getByTopicName(topicName));
        if(!topic.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Consumers> consumer = this.consumerRepository.findById(id);
        if(consumer.isPresent()) {
            this.subscriptionRepository.deleteTopicSubscriptionsByConsumersAndTopics(id, topic.get().getTopicId());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/hook/produce", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Topics> handleProduceEvent(@RequestBody ProducerPayload payload) {
        logger.debug("Producing message to Topic :: topic name :: " + payload.getTopic_name());
        try {
            InetAddress ip = InetAddress.getLocalHost();
            logger.info(Arrays.toString(ip.getAddress()));
            logger.info("------------- Connecting to IP address :: " + ip + " Host Name :: " + ip.getHostName() + " -------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(payload.getTopic_name() != null) {
            Topics currentTopic = this.topicsRepository.getByTopicName(payload.getTopic_name());
            if(currentTopic != null) {
                Set<TopicSubscriptions> topicSubscriptionsList = currentTopic.getConsumers();
                for(TopicSubscriptions topic : topicSubscriptionsList) {
                    logger.info("Consumers are :: " + topic.getConsumers().getConsumer_id());
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
