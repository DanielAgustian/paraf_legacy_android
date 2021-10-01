package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetNotifSettingsModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private NotifSettingModel data;

    public String getStatus() {
        return status;
    }

    public NotifSettingModel getData() {
        return data;
    }
}
