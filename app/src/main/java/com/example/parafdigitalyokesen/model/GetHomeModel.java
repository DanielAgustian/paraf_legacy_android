package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetHomeModel {
    @SerializedName("success")
    private String success;

    @SerializedName("data")
    private StatHomeModel home;

    @SerializedName("notification_status")
    private int notifStatus;

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

    public int getNotifStatus() {
        return notifStatus;
    }
}
