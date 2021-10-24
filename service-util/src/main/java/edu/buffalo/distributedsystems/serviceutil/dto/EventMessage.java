package edu.buffalo.distributedsystems.serviceutil.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage {
    private String eventName;
    private String message;
    private String websiteUrl;
    private String websiteName;
    private String websiteId;
    private int status;
    private String createdOn;
}