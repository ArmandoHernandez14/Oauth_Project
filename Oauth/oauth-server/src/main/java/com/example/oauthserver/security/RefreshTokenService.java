package com.example.oauthserver.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;

import java.util.Date;

@Service
public class RefreshTokenService {

    private final Key key =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long REFRESH_EXPIRATION =
            1000L * 60 * 60 * 24 * 7;

    public String generateRefreshToken(
            UserDetails user
    ){

        return Jwts.builder()

                .subject(user.getUsername())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + REFRESH_EXPIRATION
                        )
                )

                .signWith(key)

                .compact();

    }

}