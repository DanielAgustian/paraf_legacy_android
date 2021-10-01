package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetSignNumberModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private SignNumberModel home;

    public String getStatus() {
        return status;
    }

    public SignNumberModel getHome() {
        return home;
    }
}
