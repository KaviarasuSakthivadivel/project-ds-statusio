package edu.buffalo.distributedsystems.messageconsumer.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage {
    private String topic_name;
    private String producer_name;
    private String message;
}
