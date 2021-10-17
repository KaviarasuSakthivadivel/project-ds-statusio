package edu.buffalo.distributedsystems.incidenttracker;

import edu.buffalo.distributedsystems.incidenttracker.core.CronJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IncidentTrackerApplication {
    private static final Logger logger = LoggerFactory.getLogger(IncidentTrackerApplication.class);
    public static void main(String[] args) {
        logger.info("---------------------Incident Tracker App started---------------------------");
        SpringApplication.run(IncidentTrackerApplication.class, args);
    }

}
