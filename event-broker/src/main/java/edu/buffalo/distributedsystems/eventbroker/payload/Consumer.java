package edu.buffalo.distributedsystems.eventbroker.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consumer {
    private String name;
    private String domain;
    private String protocol;
    private String port;
}
