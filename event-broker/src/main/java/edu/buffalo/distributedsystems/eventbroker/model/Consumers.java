package edu.buffalo.distributedsystems.eventbroker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "consumers")
public class Consumers {
    @Id
    @Column(name = "consumer_id")
    private String consumer_id;

    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean is_active;

    @OneToMany(mappedBy = "consumers")
    @JsonManagedReference
    private Set<TopicSubscriptions> subscriptions;
}
