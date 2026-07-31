package main.java.com.example.oauthserver.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import main.java.com.example.oauthserver.entity.User;
import main.java.com.example.oauthserver.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    public UserService(

            UserRepository repository,

            PasswordEncoder encoder) {

        this.repository = repository;

        this.encoder = encoder;

    }

    public User register(

            String username,

            String password) {

        User user = new User();

        user.setUsername(username);

        user.setPassword(

                encoder.encode(password));

        user.setRole("USER");

        return repository.save(user);

    }

}