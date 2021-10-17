package edu.buffalo.distributedsystems.incidenttracker.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "website_health")
public class WebsiteHealth {
    @Id
    private String id;

    @Column(name = "website_id", nullable = false)
    private String website_id;

    @Column(name = "tracked_at")
    @CreatedDate
    private LocalDateTime tracked_at;

    @Column(name = "response_code")
    private int response_code;

    @Column(name = "status")
    private int status;
}