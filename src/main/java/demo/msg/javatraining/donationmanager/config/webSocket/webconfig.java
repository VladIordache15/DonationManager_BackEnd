package demo.msg.javatraining.donationmanager.config.webSocket;

import demo.msg.javatraining.donationmanager.controller.notification.BotificationWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(botificationWebSocketHandler(), "/stocks/{id}").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler botificationWebSocketHandler() {
        return new BotificationWebSocketHandler();
    }
}
