package edu.buffalo.distributedsystems.messageconsumer.api;

import edu.buffalo.distributedsystems.messageconsumer.payload.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/consumer/consume", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EventMessage> consumeMessage(@RequestBody EventMessage eventMessage) {
        logger.debug("Consuming message :: {0} ", new String[]{eventMessage.getMessage()});
        return new ResponseEntity<>(eventMessage, HttpStatus.OK);
    }
}
