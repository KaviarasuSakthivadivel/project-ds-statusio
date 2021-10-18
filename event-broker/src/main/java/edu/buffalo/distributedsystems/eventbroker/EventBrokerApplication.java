package edu.buffalo.distributedsystems.eventbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("edu.buffalo.distributedsystems.eventbroker.model")
public class EventBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventBrokerApplication.class, args);
    }

}
