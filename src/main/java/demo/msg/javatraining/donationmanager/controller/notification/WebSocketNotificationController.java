package demo.msg.javatraining.donationmanager.controller.notification;

import demo.msg.javatraining.donationmanager.controller.test.Greeting;
import demo.msg.javatraining.donationmanager.controller.test.HelloMessage;
import demo.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import demo.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/socket")

public class WebSocketNotificationController {
    @Autowired
    NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

//    @MessageMapping("/notification.publish")
//    @SendTo("/topic/public")
//    public void publishWebSocket(@Payload Map<String, Long> payload){
//        Long userId = payload.get("userId");
//
//        List<Notification> notifications = notificationService.getNotificationsNotAppearedOnView(userId);
//        notifications.stream()
//                .map(Notification::getId)
//                .forEach(id -> notificationService.markNotificationAsAppeared(id));
//
//        List<NotificationDTO> listaDTO = NotificationConverter.convertToDTOs(notifications);
//        simpMessagingTemplate.convertAndSend("/topic/data.publish", listaDTO);
//
//
//    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }



    @MessageMapping("/notification.all")
    @SendTo("/topic/public")
    public void getAllNotifications(@Payload Map<String, Long> payload){

        Long userId = payload.get("userId");

        List<Notification> notifications = notificationService.getAllNotifications(userId);
        List<NotificationDTO> listaDTO = NotificationConverter.convertToDTOs(notifications);
        simpMessagingTemplate.convertAndSend("/topic/data.response", listaDTO);
//        return NotificationConverter.convertToDTOs(notifications);


    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
