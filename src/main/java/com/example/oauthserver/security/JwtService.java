package com.example.oauthserver.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final JwtProperties properties;
    public JwtService(JwtProperties properties) {this.properties = properties;}
    /**
     * Returns the signing key used for both generating
     * and validating JWTs.
     */
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));}
    public String generateToken(UserDetails user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + properties.getAccessExpiration())
                ).signWith(getSigningKey()).compact();
    }

    public String extractUsername(String token) { return extractClaims(token).getSubject();}

    public boolean isTokenValid(String token, UserDetails user) 
    {String username = extractUsername(token);
    return username.equals(user.getUsername()) && !isTokenExpired(token); }

    private boolean isTokenExpired(String token) {
    return extractClaims(token)
            .getExpiration()
            .before(new Date()); }
    
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
