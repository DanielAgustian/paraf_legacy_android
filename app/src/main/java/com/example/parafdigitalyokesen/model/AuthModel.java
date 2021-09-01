package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class AuthModel {
    @SerializedName("email")
    String email;

    @SerializedName("name")
    String name;

    @SerializedName("password")
    String password;

    @SerializedName("password_confirmation")
    String password_confirmation;

    public AuthModel(String email, String name, String password, String password_confirmation){
            this.email = email;
            this.name = name;
            this.password = password;
            this.password_confirmation = password_confirmation;
    }
    public String getEmail(){
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return password_confirmation;
    }
}
