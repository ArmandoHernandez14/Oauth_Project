package com.example.oauthserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    /*
     * IMPORTANT:
     * This key must be at least 32 bytes for HS256.
     * We'll move it into application.properties later.
     */
    private static final String SECRET =
            "my-super-secret-key-change-this-in-production-123456789";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    public String generateToken(UserDetails user) {

        long now = System.currentTimeMillis();

        return Jwts.builder().subject(user.getUsername()).issuedAt(new Date(now)).expiration(new Date(now + 1000L * 60 * 60 * 24)).signWith(key).compact();
    }
    public String extractUsername(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}