package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class AuthModel {
    @SerializedName("email")
    String email;

    @SerializedName("name")
    String name;

    @SerializedName("password")
    String password;

    public AuthModel(String email, String name, String password){
            this.email = email;
            this.name = name;
            this.password = password;

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

}
