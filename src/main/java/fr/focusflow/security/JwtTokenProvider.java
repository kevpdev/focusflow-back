package fr.focusflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Clé secrète encodée en Base64 (a stoker plus tard en variable d'environnement)
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    // Générer la clé de signature à partir de la clé encodée en Base64
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Générer un token JWT avec le sujet (email de l'utilisateur)
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + Long.parseLong(jwtExpiration));  // Token valide pendant 24 heures

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // Utiliser la clé signée avec HS512
                .compact();
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
}
