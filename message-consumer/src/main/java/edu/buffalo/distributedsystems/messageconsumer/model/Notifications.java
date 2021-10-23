package edu.buffalo.distributedsystems.messageconsumer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notifications {
    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String user_id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "website_url", nullable = false)
    private String website_url;

    @Column(name = "website_name", nullable = false)
    private String website_name;

    @Column(name = "tracked_at", nullable = false)
    @CreatedDate
    private LocalDateTime date;

    @Column(name = "status", nullable = false)
    private String status;
}
