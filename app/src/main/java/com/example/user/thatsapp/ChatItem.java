package com.example.user.thatsapp;

/**
 * Created by User on 2/20/2017.
 */

public class ChatItem {
    private String message;
    private String email;
    private String key;
    private String time;

    public ChatItem(String message, String email, String key, String time) {
        this.message = message;
        this.email = email;
        this.key = key;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
