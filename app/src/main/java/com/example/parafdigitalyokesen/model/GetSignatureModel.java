package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSignatureModel {
    @SerializedName("status")
    String status;

    @SerializedName("data")
    List<SignModel> data;

    public GetSignatureModel(String status, List<SignModel> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public List<SignModel> getData() {
        return data;
    }
}
