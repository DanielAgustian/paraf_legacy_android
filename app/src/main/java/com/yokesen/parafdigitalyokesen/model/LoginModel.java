package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("access_token")
    String token;

    public LoginModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
