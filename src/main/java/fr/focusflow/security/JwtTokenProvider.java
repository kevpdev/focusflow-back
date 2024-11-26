package fr.focusflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Clé secrète encodée en Base64 (a stoker plus tard en variable d'environnement)
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expiration}")
    private String jwtTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private String jwtRefreshTokenExpiration;

    // Générer la clé de signature à partir de la clé encodée en Base64
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Génère un token JWT (access ou refresh) avec l'email de l'utilisateur et la durée d'expiration spécifiée
     *
     * @param email              L'email de l'utilisateur
     * @param expirationDuration La durée d'expiration du token en millisecondes
     * @return un token au format String
     */
    private String generateTokenWithExpiration(String email, long expirationDuration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationDuration * 1000);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // Utiliser la clé signée avec HS512
                .compact();
    }

    /**
     * Génère un token JWT (access token) avec l'email de l'utilisateur
     *
     * @param email L'email de l'utilisateur
     * @return un token au format String
     */
    public String generateToken(String email) {
        return generateTokenWithExpiration(email, Long.parseLong(jwtTokenExpiration));
    }

    /**
     * Génère un refresh token JWT avec l'email de l'utilisateur
     *
     * @param email L'email de l'utilisateur
     * @return un refresh token au format String
     */
    public String generateRefreshToken(String email) {
        return generateTokenWithExpiration(email, Long.parseLong(jwtRefreshTokenExpiration));
    }


    // Extraire l'email (ou identifiant) du token JWT
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())  // Utilise la clé pour parser et valider le token
                .build()  // Construit le parser avec la configuration
                .parseClaimsJws(token)  // Analyse le token JWT
                .getBody();  // Obtenir le contenu (claims)

        return claims.getSubject();  // Retourne l'email (le "subject")
    }

    // Valider le token JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())  // Clé pour valider la signature
                    .build()
                    .parseClaimsJws(token);  // Valider le token JWT
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Si le token est invalide ou expiré, une exception est levée
            return false;
        }
    }

    /**
     * Extract refresh Token from cookie
     *
     * @param request
     * @return a string cookie value or null
     */
    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
