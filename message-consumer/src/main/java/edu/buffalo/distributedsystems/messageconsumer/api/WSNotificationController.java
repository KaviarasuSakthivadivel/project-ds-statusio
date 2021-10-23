package edu.buffalo.distributedsystems.messageconsumer.api;

import edu.buffalo.distributedsystems.messageconsumer.payload.Notification;
import edu.buffalo.distributedsystems.messageconsumer.payload.UserMessage;
import edu.buffalo.distributedsystems.messageconsumer.repository.NotificationsRepository;
import edu.buffalo.distributedsystems.messageconsumer.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class WSNotificationController {

    private final NotificationService service;

    public WSNotificationController(NotificationService service, NotificationsRepository repository) {
        this.service = service;
    }

    @MessageMapping("/connect")
    @SendTo("/topic/notifications")
    public Notification greeting(UserMessage message) throws Exception {
        service.addUserName(message.getEmail());
        Thread.sleep(1000);
        return new Notification("Hello, " + HtmlUtils.htmlEscape(message.getEmail()) + "!");
    }
}
