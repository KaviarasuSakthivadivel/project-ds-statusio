package edu.buffalo.distributedsystems.statusio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatusIoApplication {
	private static final Logger logger = LoggerFactory.getLogger(StatusIoApplication.class);

	public static void main(String[] args) {
		System.out.println("Coming in system print");
		logger.info("Start the status io app server");
		SpringApplication.run(StatusIoApplication.class, args);
	}

}
