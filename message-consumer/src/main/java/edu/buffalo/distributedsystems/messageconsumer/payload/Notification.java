package edu.buffalo.distributedsystems.messageconsumer.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Notification {
    private String content;
    private String websiteName;
    private String websiteUrl;
    private String status;
    private String date;
    private String email;

    public Notification(String content) {
        this.content = content;
    }
}
