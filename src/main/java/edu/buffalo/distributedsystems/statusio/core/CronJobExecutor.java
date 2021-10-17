package edu.buffalo.distributedsystems.statusio.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CronJobExecutor {

    private final Logger logger = LoggerFactory.getLogger(CronJobExecutor.class);

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void scheduleFixedDelayTask() {
        long now = System.currentTimeMillis() / 1000;
        logger.info("Fixed rate task with one second initial delay - " + now);
    }
}
