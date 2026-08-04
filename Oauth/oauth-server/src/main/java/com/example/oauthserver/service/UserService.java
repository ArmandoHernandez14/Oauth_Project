package com.example.oauthserver.service;


import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.oauthserver.entity.User;
import com.example.oauthserver.repository.FileUserRepository;


@Service
public class UserService {


    private final FileUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public UserService(
            FileUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }




    public User register(
            String username,
            String password
    ) {


        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException(
                    "Username already exists"
            );
        }


        User user = new User();

        user.setUsername(username);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRole("ROLE_USER");


        return userRepository.save(user);

    }




    public List<User> getUsers(){

        return userRepository.findAll();

    }

}