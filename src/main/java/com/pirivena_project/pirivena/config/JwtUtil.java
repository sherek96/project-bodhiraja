package com.pirivena_project.pirivena.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // IMPORTANT: In production, store this securely in application.properties
    // For HS256, the secret key string MUST be at least 256 bits (32 bytes/characters) long.
    private final String SECRET_KEY_STRING = "pirivena_educational_management_system_secure_jwt_secret_key_2026";

    // Converts our plain text string into a secure cryptographic SecretKey object required by JJWT 0.12+
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Extract the username (the subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Extract the expiration date to check if the passport is still active
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic helper method to parse out specific information (claims) from the token cargo bay
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Opens up the token, checks the signature using our secret key, and reads the payload
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Modern JJWT 0.12+ signature verification syntax
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Replaced the deprecated .getBody() method
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 3. Generates a brand-new token upon a successful admin/teacher login
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Grab the user's role from Spring Security and inject it straight into the token cargo bay
        claims.put("roles", userDetails.getAuthorities());

        return createToken(claims, userDetails.getUsername());
    }

    // Assembles the JWT blueprint parts and signs them
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token valid for 10 Hours
                .signWith(getSigningKey()) // Cryptographically sign the token
                .compact(); // Pack it into a flat compressed string
    }

    // 4. Validates the token coming in from React against the user database record
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}