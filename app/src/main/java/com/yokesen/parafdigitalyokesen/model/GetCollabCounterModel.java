package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetCollabCounterModel {
    @SerializedName("status")
    String status;

    @SerializedName("data")
    CollabCounterModel data;


    public String getStatus() {
        return status;
    }

    public CollabCounterModel getData() {
        return data;
    }
}
