package com.example.rxjava.services;

import com.example.rxjava.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private List<User> users = new ArrayList();

    public UserService() {
        users.add(new User("admin", "superSecret"));
        users.add(new User("root", "superExtraSecret"));
        users.add(new User("lrecio", "notSecret"));
    }

    public List findAll() {
        return users;
    }

    public Optional findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void create(User user) {
        users.add(user);
    }
}