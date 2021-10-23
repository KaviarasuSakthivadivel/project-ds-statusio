package edu.buffalo.distributedsystems.serviceutil.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ConsumerMessage {
    private String userId;
    private String emailId;
    private String websiteUrl;
    private String websiteName;
    private String websiteId;
    private int status;
    private String createdOn;
}
