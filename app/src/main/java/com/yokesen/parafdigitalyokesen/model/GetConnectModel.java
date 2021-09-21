package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetConnectModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<ContactModel> data;

    public String getStatus() {
        return status;
    }

    public List<ContactModel> getData() {
        return data;
    }
}
