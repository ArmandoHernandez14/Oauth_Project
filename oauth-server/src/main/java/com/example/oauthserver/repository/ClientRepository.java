package com.example.oauthserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import main.java.com.example.oauthserver.entity.OAuthClient;

public interface ClientRepository extends JpaRepository<OAuthClient,Long>{

    Optional<OAuthClient> findByClientId(String clientId);

}