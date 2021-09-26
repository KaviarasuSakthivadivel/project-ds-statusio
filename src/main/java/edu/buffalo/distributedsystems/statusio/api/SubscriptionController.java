package edu.buffalo.distributedsystems.statusio.api;

import edu.buffalo.distributedsystems.statusio.model.Subscriber;
import edu.buffalo.distributedsystems.statusio.payload.SubscriptionUser;
import edu.buffalo.distributedsystems.statusio.repository.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/subscriber")
public class SubscriptionController {
    private final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private SubscriberRepository repository;

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Subscriber> addSubscriber(@RequestBody SubscriptionUser subscriptionUser) {
        logger.debug("Adding subscriber into db :: {0} ", new String[]{subscriptionUser.getEmail()});
        Subscriber subscriber = new Subscriber();
        subscriber.setFirstName(subscriptionUser.getFirstName());
        subscriber.setLastName(subscriptionUser.getLastName());
        subscriber.setEmailId(subscriptionUser.getEmail());
        subscriber.setId(UUID.randomUUID().toString());
        this.repository.save(subscriber);
        return new ResponseEntity<>(subscriber, HttpStatus.OK);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Subscriber>> queryAllCustomers() {
        List<Subscriber> allSubscribers = this.repository.findAll();
        logger.info("Number of Subscribers: " + allSubscribers.size());
        return new ResponseEntity<>(allSubscribers, HttpStatus.OK);
    }

}
