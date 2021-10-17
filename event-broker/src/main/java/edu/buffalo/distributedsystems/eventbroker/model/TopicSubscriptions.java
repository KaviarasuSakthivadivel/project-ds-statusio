package edu.buffalo.distributedsystems.eventbroker.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "topic_subscriptions")
public class TopicSubscriptions {
    @Id
    @Column(name = "topic_subscription_id")
    private String topic_subcription_id;

    @ManyToOne
    @JoinColumn(name = "consumer_id")
    private Consumers consumers;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topics topics;
}