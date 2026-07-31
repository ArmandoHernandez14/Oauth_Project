package com.example.oauthserver.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name="refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(length=2000, unique=true)
    private String token;

    private Instant expiresAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private OAuthClient client;

    public RefreshToken(){}

    public Long getId(){ return id; }

    public void setId(Long id){
        this.id=id;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }

    public Instant getExpiresAt(){
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt){
        this.expiresAt=expiresAt;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user=user;
    }

    public OAuthClient getClient(){
        return client;
    }

    public void setClient(OAuthClient client){
        this.client=client;
    }

}