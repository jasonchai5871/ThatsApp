package com.example.user.thatsapp;

import android.app.Application;
import android.provider.Settings;

/**
 * Created by User on 2/18/2017.
 */

public class UserDatabaseName {
    private String email;


    public String getEmail() {
        return email;
    }
    //Convert the email to a valid firebase database url name
    //Example user.123@email.com to user_123_email_com
    public void setEmail(String email) {
        this.email = email;
    }

    public String convertEmailToDbName(String email){
        String a[], name = "";

        a = email.split("@|\\.");
        for(int i = 0; i < a.length;i++){
            if(i != a.length - 1)
                name = name + a[i] + "_";
            else
                name = name + a[i];
        }
        return name;
    }

}
