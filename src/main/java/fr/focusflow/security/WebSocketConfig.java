package fr.focusflow.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }


    /**
     * Configure les endpoints WebSocket auxquels les clients peuvent se connecter pour initier la communication.
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wsocket")   // Ajoute un endpoint WebSocket, que le client peut utiliser pour se connecter
                .addInterceptors(jwtHandshakeInterceptor)  // Ajoute l'intercepteur JWT
                .setAllowedOriginPatterns("*")  // Permet toutes les origines, à restreindre en production
                .withSockJS(); // Utiliser SockJS pour fallback si WebSocket n'est pas supporté. A desactiver pour test POSTMAN
    }

    /**
     * Configure le broker de messages qui gère les messages envoyés et reçus par le serveur WebSocket.
     * Le broker de messages est chargé de diffuser les messages entre les clients et le serveur.
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")  // Préfixe pour les messages du client
                .enableSimpleBroker("/topic"); // Préfixe pour les messages envoyés du serveur
    }
}
