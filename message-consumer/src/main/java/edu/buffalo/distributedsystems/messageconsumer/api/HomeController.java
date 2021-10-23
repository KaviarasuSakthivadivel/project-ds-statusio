package edu.buffalo.distributedsystems.messageconsumer.api;

import edu.buffalo.distributedsystems.messageconsumer.model.Notifications;
import edu.buffalo.distributedsystems.messageconsumer.repository.NotificationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final NotificationsRepository repository;

    @Autowired
    public HomeController(NotificationsRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Notifications>> getNotifications() {
        List<Notifications> list = this.repository.findAll();
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/notifications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Notifications>> getNotification(@PathVariable String id) {
        List<Notifications> list = this.repository.findAll();
        List<Notifications> userNotification = new ArrayList<>();
        list.forEach((n) -> {
            if(n.getUser_id().equals(id)) {
                userNotification.add(n);
            }
        });
        return new ResponseEntity<>(userNotification, HttpStatus.ACCEPTED);
    }
}
