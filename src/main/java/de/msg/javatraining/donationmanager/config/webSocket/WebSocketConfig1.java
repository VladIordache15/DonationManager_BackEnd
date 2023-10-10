package de.msg.javatraining.donationmanager.config.webSocket;

import de.msg.javatraining.donationmanager.controller.notification.NotificationWebSocketHandler1;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig1 implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationWebSocketHandler1(), "/notif/{id}").setAllowedOrigins("*");
    }

    private WebSocketHandler notificationWebSocketHandler1() {
        return new NotificationWebSocketHandler1();
    }
}
