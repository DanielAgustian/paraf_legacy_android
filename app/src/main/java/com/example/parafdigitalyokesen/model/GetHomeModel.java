package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetHomeModel {
    @SerializedName("success")
    private String success;

    @SerializedName("data")
    private StatHomeModel home;

    public GetHomeModel(String success, StatHomeModel home) {
        this.success = success;
        this.home = home;
    }

    public String getSuccess() {
        return success;
    }

    public StatHomeModel getHome() {
        return home;
    }
}
