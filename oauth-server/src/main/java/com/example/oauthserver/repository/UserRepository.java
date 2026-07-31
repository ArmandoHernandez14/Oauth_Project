package com.example.oauthserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import main.java.com.example.oauthserver.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{

    Optional<User> findByUsername(String username);

}