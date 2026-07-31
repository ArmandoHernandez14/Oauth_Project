package com.example.oauthserver.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import main.java.com.example.oauthserver.entity.User;
import main.java.com.example.oauthserver.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(
            UserRepository repository) {

        this.repository = repository;

    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        User user = repository

                .findByUsername(username)

                .orElseThrow(() ->
                        new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(

                user.getUsername(),

                user.getPassword(),

                Collections.emptyList()

        );

    }

}