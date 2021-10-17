package edu.buffalo.distributedsystems.incidenttracker.repository;

import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteHealth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteHealthRepository extends JpaRepository<WebsiteHealth, String> {
}
