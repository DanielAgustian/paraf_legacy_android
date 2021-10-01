package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class SignNumberModel {
    @SerializedName("user_id")
    int userId;

    @SerializedName("counter")
    int counter;

    public int getUserId() {
        return userId;
    }

    public int getCounter() {
        return counter;
    }
}
