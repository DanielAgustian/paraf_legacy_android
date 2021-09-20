package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSaveAllSign {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<SaveSignModel> data;

    public String getStatus() {
        return status;
    }

    public List<SaveSignModel> getData() {
        return data;
    }
}
