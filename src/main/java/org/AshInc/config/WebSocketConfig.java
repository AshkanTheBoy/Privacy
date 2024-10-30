package org.AshInc.config; // Define the package for this configuration class

import org.springframework.context.annotation.Configuration; // Import the Configuration annotation
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // Import MessageBrokerRegistry for configuring the message broker
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // Import annotation to enable WebSocket message broker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // Import StompEndpointRegistry for registering STOMP endpoints
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // Import interface for WebSocket message broker configuration

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
