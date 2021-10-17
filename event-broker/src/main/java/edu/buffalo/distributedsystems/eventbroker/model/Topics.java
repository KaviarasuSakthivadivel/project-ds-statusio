package edu.buffalo.distributedsystems.eventbroker.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "topics")
public class Topics {
    @Id
    @Column(name = "topic_id")
    private String topic_id;

    @Column(name = "topic_name", unique = true)
    private String topic_name;
}
