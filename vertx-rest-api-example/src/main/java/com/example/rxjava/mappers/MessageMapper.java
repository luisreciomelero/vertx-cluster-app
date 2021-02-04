package com.example.rxjava.mappers;

import com.example.rxjava.messages.UserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageMapper {

    private ObjectMapper mapper;

    public MessageMapper() {
        mapper = new ObjectMapper();
    }

    public String messageToJson(UserMessage message) throws JsonProcessingException {
        return mapper.writeValueAsString(message);
    }

    public UserMessage jsonToUserMessage(String jsonString) {
        return mapper.convertValue(jsonString, UserMessage.class);
    }
}
