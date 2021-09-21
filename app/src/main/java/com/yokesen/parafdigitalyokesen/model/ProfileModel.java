package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    public ProfileModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
