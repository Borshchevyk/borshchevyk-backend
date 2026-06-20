package ru.kubsu.borshchevyk.sync.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for WebSocket message broker.
 *
 * @author Aleksey Timko
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Topic for pushing sync events to clients
        config.enableSimpleBroker("/topic");
        
        // Prefix for messages sent from clients to the server (if needed in the future)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint clients will use to connect to the WebSocket server
        registry.addEndpoint("/ws-sync")
                .setAllowedOriginPatterns("*") // TODO: restrict origins in production
                .withSockJS(); // Enable SockJS fallback for older browsers
    }
}
