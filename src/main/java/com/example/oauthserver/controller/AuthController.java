// package com.example.oauthserver.controller;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import com.example.oauthserver.entity.User;
// import com.example.oauthserver.service.UserService;
// @RestController
// @RequestMapping("/auth")
// public class AuthController {
//     private final UserService userService;
//     public AuthController(UserService userService){ this.userService = userService;}
//     @PostMapping("/register")
//     public User register(@RequestBody RegisterRequest request)
//     {  return userService.register(request.username(),request.password());}
//     public record RegisterRequest(String username,String password){}
// }
package com.example.oauthserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauthserver.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody RegisterRequest request) {

        userService.register(
                request.username(),
                request.password()
        );

        return new RegisterResponse(
                request.username(),
                "Account created successfully"
        );
    }

    public record RegisterRequest(
            String username,
            String password
    ) {}

    public record RegisterResponse(
            String username,
            String message
    ) {}
}