package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCollabViewModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private CollabViewModel data;

    public String getStatus() {
        return status;
    }

    public CollabViewModel getData() {
        return data;
    }
}
