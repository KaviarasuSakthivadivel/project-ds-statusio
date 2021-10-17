package edu.buffalo.distributedsystems.incidenttracker.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsiteMonitorPayload {
    private String name;
    private String website_url;
}
