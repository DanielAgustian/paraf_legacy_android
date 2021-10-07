package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetQRSignModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private SignatureDetailModel home;


    @SerializedName("message")
    private String message;
    public GetQRSignModel(String status, SignatureDetailModel home) {
        this.status = status;
        this.home = home;
    }

    public String getStatus() {
        return status;
    }

    public SignatureDetailModel getHome() {
        return home;
    }

    public String getMessage() {
        return message;
    }
}
