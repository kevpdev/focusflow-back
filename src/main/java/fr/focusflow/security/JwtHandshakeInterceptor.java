package fr.focusflow.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /**
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Vérifie si la requête est de type ServletServerHttpRequest
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // Récupère le JWT depuis l'en-tête Authorization
            //String token = servletRequest.getHeader("Authorization");
            String token = servletRequest.getParameter("token");
            //if (token != null && token.startsWith("Bearer ")) {
            //  token = token.substring(7); // Retire le préfixe "Bearer "
            if (token != null) {
                // Valide le token JWT
                if (jwtTokenProvider.validateToken(token)) {
                    // Si valide, ajouter l'utilisateur à la session WebSocket
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    attributes.put("userEmail", email);
                    return true; // Autoriser la connexion
                }
            }
        }
        return false; // Refuse la connexion si pas de token valide
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Pas besoin de traitement particulier après le handshake pour cette implémentation
    }
}
