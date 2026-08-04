package com.example.oauthserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProtectedApiController {

    @GetMapping("/api/hello")
    public String hello() {

        return "Welcome to the protected API!";

    }

}