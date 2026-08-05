package com.example.oauthserver.controller;
import com.example.oauthserver.security.JwtService;
import com.example.oauthserver.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginController(
        AuthenticationManager authenticationManager,
        JwtService jwtService, 
        RefreshTokenService refreshTokenService    
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }
    @PostMapping("/login")
    public LoginResponse login(
        @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(),request.password()));
        UserDetails user = (UserDetails) authentication.getPrincipal();
        // String token = jwtService.generateToken(user);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user);
        return new LoginResponse(accessToken, refreshToken, "Bearer", 3600, user.getUsername()); 
    }
    public record LoginRequest(String username,String password) {}
    public record LoginResponse(String accessToken,String refreshToken,String tokenType,long expiresIn,String username){}
}