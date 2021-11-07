package edu.buffalo.distributedsystems.eventbrokerb.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerPayload {
    private String topic_name;
    private String producer_name;
    private String message;
}
