package edu.buffalo.distributedsystems.eventbrokerb.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriberPayload {
    private String firstName;
    private String lastName;
    private String email;
}
