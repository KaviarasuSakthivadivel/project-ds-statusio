package edu.buffalo.distributedsystems.messageproducer.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage {
    private String eventName;
    private String message;
}
