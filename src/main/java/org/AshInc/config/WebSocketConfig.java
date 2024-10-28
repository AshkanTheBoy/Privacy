package org.AshInc.config; // Define the package for this configuration class

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Annotation to indicate that this class contains configuration settings
@Configuration
// Annotation to enable WebSocket message broker support
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // Override method to configure the message broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple message broker for publishing and subscribing to messages
        config.enableSimpleBroker("/topic");
        // Set the prefix for application destination paths
        config.setApplicationDestinationPrefixes("/app");
    }

    // Method to register STOMP endpoints
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/chat" endpoint for WebSocket connections
        registry.addEndpoint("/chat");
        // Allow SockJS fallback options for browsers that don't support WebSockets
        registry.addEndpoint("/chat").withSockJS();
    }
}
