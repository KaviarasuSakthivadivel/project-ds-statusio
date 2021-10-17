package edu.buffalo.distributedsystems.incidenttracker.api;

import edu.buffalo.distributedsystems.incidenttracker.model.WebsiteMonitor;
import edu.buffalo.distributedsystems.incidenttracker.repository.WebsiteMonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(path = "/website/tracking")
public class WebsiteMonitorController {
    private final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final WebsiteMonitorRepository repository;

    @Autowired
    public WebsiteMonitorController(WebsiteMonitorRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WebsiteMonitor> addWebsiteMonitor(@RequestBody WebsiteMonitor websiteMonitor) {
        logger.debug("Adding website into db :: {0} ", new String[]{websiteMonitor.getWebsite_url()});
        websiteMonitor.setId(UUID.randomUUID().toString());
        this.repository.save(websiteMonitor);
        return new ResponseEntity<>(websiteMonitor, HttpStatus.OK);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<WebsiteMonitor>> queryAllWebsiteUrls() {
        List<WebsiteMonitor> allWebsiteMonitors = this.repository.findAll();
        logger.info("Number of Websites: " + allWebsiteMonitors.size());
        return new ResponseEntity<>(allWebsiteMonitors, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> deleteWebsiteMonitor(@PathVariable(value="id") String id) {
        WebsiteMonitor websiteMonitor = this.repository.getById(id);
        this.repository.delete(websiteMonitor);
        return new ResponseEntity<>(websiteMonitor.getId(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<WebsiteMonitor> getWebsiteMonitor(@PathVariable(value="id") String id) {
        Optional<WebsiteMonitor> websiteMonitor = this.repository.findById(id);
        return websiteMonitor.map(monitor -> new ResponseEntity<>(monitor, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
