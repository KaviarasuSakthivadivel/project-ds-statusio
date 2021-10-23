package edu.buffalo.distributedsystems.messageconsumer.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionUser {
    private String firstName;
    private String lastName;
    private String email;
}
