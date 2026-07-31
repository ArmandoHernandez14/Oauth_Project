package com.example.oauthserver.entity;

import jakarta.persistence.*;

@Entity
@Table(name="oauth_clients")
public class OAuthClient {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String scopes;

    private String grantTypes;

    public OAuthClient(){}

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getClientId(){
        return clientId;
    }

    public void setClientId(String clientId){
        this.clientId=clientId;
    }

    public String getClientSecret(){
        return clientSecret;
    }

    public void setClientSecret(String clientSecret){
        this.clientSecret=clientSecret;
    }

    public String getRedirectUri(){
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri){
        this.redirectUri=redirectUri;
    }

    public String getScopes(){
        return scopes;
    }

    public void setScopes(String scopes){
        this.scopes=scopes;
    }

    public String getGrantTypes(){
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes){
        this.grantTypes=grantTypes;
    }

}