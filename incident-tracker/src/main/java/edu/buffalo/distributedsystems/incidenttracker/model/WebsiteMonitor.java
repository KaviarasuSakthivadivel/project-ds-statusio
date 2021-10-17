package edu.buffalo.distributedsystems.incidenttracker.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "website_monitor")
public class WebsiteMonitor {
    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "website_url", nullable = false)
    private String website_url;
}
