package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetNotifListModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<NotificationModel> data;

    public String getStatus() {
        return status;
    }

    public List<NotificationModel> getData() {
        return data;
    }
}
