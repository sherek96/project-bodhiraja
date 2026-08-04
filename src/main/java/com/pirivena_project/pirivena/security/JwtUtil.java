package com.pirivena_project.pirivena.security;

// Purpose: Creates, reads and validates login tokens.

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

/**
 * Creates login tokens and checks that received tokens are still valid.
 */
@Component
public class JwtUtil {

    // This key signs tokens so they cannot be changed by the browser.
    // It must contain at least 32 characters.
    private final String SECRET_KEY_STRING = "pirivena_educational_management_system_secure_jwt_secret_key_2026";

    // Convert the text key into the format required by the JWT library.
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Read the username stored inside a token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Read when the token expires.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Read one selected value from the token.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Verify the signature and return all values stored in the token.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Create a token after a successful login.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Store all assigned roles in the token.
        claims.put("roles", userDetails.getAuthorities());

        return createToken(claims, userDetails.getUsername());
    }

    // Set the token owner, lifetime and signature.
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Valid for 10 hours.
                .signWith(getSigningKey())
                .compact();
    }

    // A token is valid when it belongs to this user and has not expired.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
