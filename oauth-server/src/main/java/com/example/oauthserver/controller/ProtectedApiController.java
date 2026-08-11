package com.example.oauthserver.controller;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class ProtectedApiController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Welcome to the protected API!";
    }
    @GetMapping("/api/me")
    public UserInfo me(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return new UserInfo(
                user.getUsername(),
                user.getAuthorities()
);}
    public record UserInfo(
            String username,
            Collection<?> authorities
    ) { }
}