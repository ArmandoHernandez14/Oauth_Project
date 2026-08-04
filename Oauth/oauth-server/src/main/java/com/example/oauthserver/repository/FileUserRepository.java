package com.example.oauthserver.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.oauthserver.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Repository
public class FileUserRepository {

    private static final String FILE_PATH = "src/main/resources/data/users.json";

    private final ObjectMapper objectMapper;

    private List<User> users;


    public FileUserRepository(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        loadUsers();

    }


    private void loadUsers() {

        try {

            File file = new File(FILE_PATH);


            if (!file.exists()) {

                users = new ArrayList<>();

                saveUsers();

                return;

            }


            users = objectMapper.readValue(
                    file,
                    new TypeReference<List<User>>() {}
            );


        } catch (IOException e) {

            throw new RuntimeException("Could not load users", e);

        }

    }



    private void saveUsers() {

        try {

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(
                            new File(FILE_PATH),
                            users
                    );


        } catch (IOException e) {

            throw new RuntimeException("Could not save users", e);

        }

    }



    public List<User> findAll() {

        return users;

    }
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user ->
                        user.getUsername()
                        .equals(username))
                .findFirst();
    }
    public User save(User user) {

        if(user.getId() == null) {

            long nextId = users.size() + 1;

            user.setId(nextId);

            users.add(user);

        }
        else {  
            delete(user);
            users.add(user);
        }
        saveUsers();
        return user;
    }



    public void delete(User user) {

        users.removeIf(
                existing ->
                existing.getId()
                .equals(user.getId())
        );

        saveUsers();

    }

}
