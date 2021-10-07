package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSignCounterModel {
    @SerializedName("status")
    String status;

    @SerializedName("data")
    SignCounterModel data;

    public String getStatus() {
        return status;
    }

    public SignCounterModel getData() {
        return data;
    }
}
