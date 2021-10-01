package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class NotifSettingModel {
    @SerializedName("notification")
    int notification;

    @SerializedName("request")
    int request;

    @SerializedName("accepted")
    int accepted;

    @SerializedName("rejected")
    int rejected;

    public int getNotification() {
        return notification;
    }

    public int getRequest() {
        return request;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getRejected() {
        return rejected;
    }
}
