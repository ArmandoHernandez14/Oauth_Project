package main.java.com.example.oauthserver.service;

import org.springframework.stereotype.Service;

import main.java.com.example.oauthserver.entity.OAuthClient;
import main.java.com.example.oauthserver.repository.ClientRepository;

@Service
public class OAuthClientService {

    private final ClientRepository repository;

    public OAuthClientService(ClientRepository repository){

        this.repository=repository;

    }

    public OAuthClient save(OAuthClient client){

        return repository.save(client);

    }

    public OAuthClient find(String clientId){

        return repository
                .findByClientId(clientId)
                .orElseThrow();

    }

}