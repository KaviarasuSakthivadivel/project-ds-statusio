package edu.buffalo.distributedsystems.incidenttracker.repository;

import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteMonitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteMonitorRepository extends JpaRepository<WebsiteMonitor, String> {
}
