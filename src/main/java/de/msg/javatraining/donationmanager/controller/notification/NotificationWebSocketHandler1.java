package de.msg.javatraining.donationmanager.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import de.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationWebSocketHandler1 extends TextWebSocketHandler {

    @Autowired
    private NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

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
        List<Notification> notifications = notificationService.getNotificationsNotAppearedOnView(id);
        notifications.stream()
                .map(Notification::getId)
                .forEach(userid -> notificationService.markNotificationAsAppeared(userid));

        List<NotificationDTO> dtos = NotificationConverter.convertToDTOs(notifications);

        for(NotificationDTO not: dtos) {

            TextMessage message = new TextMessage(objectMapper.writeValueAsString(not));
            session.sendMessage(message);


        }
        sessions.add(session);
    }
}
