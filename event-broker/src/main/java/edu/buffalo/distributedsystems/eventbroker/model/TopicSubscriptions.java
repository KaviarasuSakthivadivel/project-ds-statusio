package edu.buffalo.distributedsystems.eventbroker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "topic_subscriptions")
public class TopicSubscriptions {
    @Id
    @Column(name = "topic_subscription_id")
    private String topic_subscription_id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonBackReference
    private Topics topics;

    @ManyToOne
    @JoinColumn(name = "consumer_id")
    @JsonBackReference
    private Consumers consumers;

}