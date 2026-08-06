package com.example.oauthserver.controller;
import com.example.oauthserver.security.JwtService;
import com.example.oauthserver.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.example.oauthserver.security.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    public LoginController(
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        CustomUserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }
    @PostMapping("/login")
    public LoginResponse login(
        @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(),request.password()));
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user);
        return new LoginResponse(accessToken, refreshToken, "Bearer", 3600, user.getUsername()); 
    }
    @PostMapping("/refresh")
    public RefreshResponse refresh( 
    @RequestBody RefreshRequest request
    ) {
    String username = refreshTokenService.extractUsername(request.refreshToken());
    UserDetails user = userDetailsService.loadUserByUsername(username);
    if (!refreshTokenService.isTokenValid(request.refreshToken(),user)) { throw new org.springframework.security.authentication.BadCredentialsException("Invalid refresh token");}
    String newAccessToken = jwtService.generateToken(user);
    return new RefreshResponse(newAccessToken, "Bearer",3600000);
    }
    public record LoginRequest(String username,String password) {}
    public record LoginResponse(String accessToken,String refreshToken,String tokenType,long expiresIn,String username){}
    public record RefreshRequest(String refreshToken){}
    public record RefreshResponse(String accessToken, String tokenType, long expiresIn){}
}