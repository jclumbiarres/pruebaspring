package org.lumbi.ejercicio.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;

import org.lumbi.ejercicio.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String tokenPrefix;

    /**
     * Constructor de JwtTokenProvider
     * 
     * @param jwtProperties
     * 
     * @throws IllegalStateException si el secreto JWT o la expiración no están
     *                               configurados
     */
    public JwtTokenProvider(JwtProperties jwtProperties) {
        String secret = Optional.ofNullable(jwtProperties.getJwtSecret())
                .filter(s -> !s.trim().isEmpty())
                .orElseThrow(() -> new IllegalStateException(
                        "JWT secret no está configurado. Verifica app.security.jwt-secret en application.yml"));

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = Optional.ofNullable(jwtProperties.getJwtExpiration())
                .orElseThrow(() -> new IllegalStateException("JWT expiration no está configurado"))
                .toMillis();
        this.tokenPrefix = Optional.ofNullable(jwtProperties.getTokenPrefix())
                .orElse("Bearer");
    }

    /**
     * Genera un token JWT para un usuario con claims adicionales
     * 
     * @param username
     * @param claims
     * @return Token JWT como String
     */
    public String generateToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Parsea un token JWT y devuelve sus claims
     * 
     * @param token
     * @return Claims del token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida un token JWT
     * 
     * @param token
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el nombre de usuario del token JWT
     * 
     * @param token
     * @return Nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * Obtiene el prefijo del token
     * 
     * @return Prefijo del token
     */
    public String getTokenPrefix() {
        return tokenPrefix;
    }
}