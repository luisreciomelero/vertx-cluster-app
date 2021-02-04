package com.example.rxjava.mappers;


import com.example.rxjava.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserMapper {

    private ObjectMapper mapper;

    public UserMapper() {
        this.mapper = new ObjectMapper();
    }

    public String UserToJson(User user) throws JsonProcessingException {
        return mapper.writeValueAsString(user);
    }

    public User JsonToUser(String jsonString) {
        return mapper.convertValue(jsonString, User.class);
    }
}
