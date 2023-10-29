package demo.msg.javatraining.donationmanager.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.net.URI;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;


public class BotificationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Scheduled(fixedDelay = 2000)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {




        super.afterConnectionEstablished(session);

        // Get the URI from the WebSocket session
        URI uri = session.getUri();

        // Get the path from the URI
        assert uri != null;
        String path = uri.getPath();

        // Split the path into segments
        String[] pathSegments = path.split("/");

        // Assuming that 'id' is the last segment in the path
        Long id = null;
        if (pathSegments.length > 0) {
            id = Long.valueOf(pathSegments[pathSegments.length - 1]);
        }


        // Now 'id' contains the value from the 'id' parameter in the URL
        System.out.println("Received 'id' from WebSocket URL: " + id);
//        List<Notification> notifications = notificationService.getAllNotifications(id);
//
//        List<NotificationDTO> dtos = NotificationConverter.convertToDTOs(notifications);
//
//        for(NotificationDTO not: dtos) {
//
//            TextMessage message = new TextMessage(objectMapper.writeValueAsString(not));
//            session.sendMessage(message);
//
//
//        }
//        sessions.add(session);
    }

}