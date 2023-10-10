package de.msg.javatraining.donationmanager.controller.notification;

import de.msg.javatraining.donationmanager.controller.test.Greeting;
import de.msg.javatraining.donationmanager.controller.test.HelloMessage;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import de.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@RestController
@RequestMapping("/socket")

public class WebSocketNotificationController {
    @Autowired
    NotificationService notificationService;

//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/notify/{userId}")
    public List<Notification> publishWebSocket(@PathVariable Long userId){

        List<Notification> notifications = notificationService.getNotificationsNotAppearedOnView(userId);
        notifications.stream()
                .map(Notification::getId)
                .forEach(id -> notificationService.markNotificationAsAppeared(id));

        return notifications;
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inbox/{userId}")
    public List<Notification> getAllNotifications(@PathVariable Long userId){

        List<Notification> notifications = notificationService.getAllNotifications(userId);
//        simpMessagingTemplate.convertAndSend("/topic/data.response", notifications);    //le trimitem direct la frontend
        return notifications;


    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
