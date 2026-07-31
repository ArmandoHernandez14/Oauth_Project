package main.java.com.example.oauthserver.controller;

import org.springframework.web.bind.annotation.*;

import main.java.com.example.oauthserver.service.UserService;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserService service;

    public RegisterController(

            UserService service) {

        this.service = service;

    }

    @PostMapping
    public String register(

            @RequestParam String username,

            @RequestParam String password) {

        service.register(

                username,

                password);

        return "User created.";

    }

}