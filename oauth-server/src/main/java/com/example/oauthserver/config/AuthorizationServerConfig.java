package com.example.oauthserver.config;

import java.beans.Customizer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient client = RegisteredClient
                .withId(UUID.randomUUID().toString())

                .clientId("my-client")

                .clientSecret("{noop}secret")

                .clientAuthenticationMethod(
                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                .authorizationGrantType(
                        AuthorizationGrantType.AUTHORIZATION_CODE)

                .authorizationGrantType(
                        AuthorizationGrantType.REFRESH_TOKEN)

                .redirectUri("http://127.0.0.1:8081/login/oauth2/code")

                .scope("read")
                .scope("write")

                .build();

        return new InMemoryRegisteredClientRepository(client);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        return AuthorizationServerSettings.builder().build();

    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {

        KeyPair pair = generateRSAKey();

        RSAPublicKey publicKey =
                (RSAPublicKey) pair.getPublic();

        RSAPrivateKey privateKey =
                (RSAPrivateKey) pair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)

                .privateKey(privateKey)

                .keyID(UUID.randomUUID().toString())

                .build();

        JWKSet set = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(set);

    }

    @Bean
    public JwtDecoder jwtDecoder(
            JWKSource<SecurityContext> jwkSource) {

        return OAuth2AuthorizationServerConfiguration
                .jwtDecoder(jwkSource);

    }

    private static KeyPair generateRSAKey() {

        try {

            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            return generator.generateKeyPair();

        }

        catch(Exception ex) {

            throw new IllegalStateException(ex);

        }

    }

}