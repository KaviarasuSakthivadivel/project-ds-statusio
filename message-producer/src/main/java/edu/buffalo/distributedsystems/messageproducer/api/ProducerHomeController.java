package edu.buffalo.distributedsystems.messageproducer.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/incoming/webhook")
public class ProducerHomeController {

//    private final Logger logger = LoggerFactory.getLogger(ProducerHomeController.class);
//
//
//
//    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<String> handleIncomingMessage(@RequestBody EventMessage eventMessage) {
//        logger.debug("Incoming message received for the event name :: {0} ", new String[]{eventMessage.getEventName()});
//
//        return new ResponseEntity<>("", HttpStatus.OK);
//    }

}
