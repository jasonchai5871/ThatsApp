package com.example.user.thatsapp;

/**
 * Created by User on 2/28/2017.
 */

public class InboxItem {

    private String email;
    private String message;

    public InboxItem(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
