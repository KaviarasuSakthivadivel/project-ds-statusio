package edu.buffalo.distributedsystems.incidenttracker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class DoHTTPRequest {
    private static final Logger logger = LoggerFactory.getLogger(DoHTTPRequest.class);
    public static String doPost(Map<String, Object> requestParams) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/incoming/webhook";

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        logger.info("reponse from HTTP " + response);

        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }
}
