package edu.buffalo.distributedsystems.eventbroker.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consumer {
    private String first_name;
    private String last_name;
    private String email;
}
