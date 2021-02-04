package com.example.rxjava.messages;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private String action;
    private String user;

    public UserMessage() {

    }

    public UserMessage(String action, String user) {
        this.action = action;
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "action='" + action + '\'' +
                ", user=" + user +
                '}';
    }
}
