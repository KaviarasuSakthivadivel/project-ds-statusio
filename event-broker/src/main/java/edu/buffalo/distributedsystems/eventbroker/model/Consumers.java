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

    @Column(name = "first_name", columnDefinition = "varchar(255)")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "varchar(255)")
    private String last_name;

    @Column(name = "email_id", columnDefinition = "varchar(255)")
    private String emailId;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive;

    @OneToMany(mappedBy = "consumers", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<TopicSubscriptions> subscriptions;
}
