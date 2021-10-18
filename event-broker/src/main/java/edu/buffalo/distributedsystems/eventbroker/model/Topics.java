package edu.buffalo.distributedsystems.eventbroker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "topics")
public class Topics {
    @Id
    @Column(name = "topic_id")
    private String topicId;

    @Column(name = "topic_name", unique = true)
    private String topicName;

    @OneToMany(mappedBy = "topics")
    @JsonManagedReference
    private Set<TopicSubscriptions> consumers;
}
