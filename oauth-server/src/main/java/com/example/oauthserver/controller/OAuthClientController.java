package com.example.oauthserver.controller;

import org.springframework.web.bind.annotation.*;

import main.java.com.example.oauthserver.entity.OAuthClient;
import main.java.com.example.oauthserver.service.OAuthClientService;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final OAuthClientService service;

    public ClientController(
            OAuthClientService service){

        this.service=service;

    }

    @PostMapping

    public OAuthClient register(

            @RequestBody OAuthClient client){

        return service.save(client);

    }

}