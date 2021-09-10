package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMyReqDetailModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private MyReqDetailModel data;

    public String getStatus() {
        return status;
    }

    public MyReqDetailModel getData() {
        return data;
    }
}
