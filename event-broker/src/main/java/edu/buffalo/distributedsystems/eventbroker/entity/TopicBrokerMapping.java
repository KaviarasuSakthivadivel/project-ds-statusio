package edu.buffalo.distributedsystems.eventbroker.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("TopicBrokers")
@Getter
@Setter
public class TopicBrokerMapping implements Serializable {
    private String id;
    private String topicName;

    public TopicBrokerMapping(String id, String topicName) {
        this.id = id;
        this.topicName = topicName;
    }
}
