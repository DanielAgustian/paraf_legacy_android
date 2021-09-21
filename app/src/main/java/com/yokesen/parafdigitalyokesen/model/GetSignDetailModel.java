package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetSignDetailModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private SignatureDetailModel home;

    public GetSignDetailModel(String status, SignatureDetailModel home) {
        this.status = status;
        this.home = home;
    }

    public String getStatus() {
        return status;
    }

    public SignatureDetailModel getHome() {
        return home;
    }
}
