package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPasscodeModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private PasscodeModel data;

    public String getStatus() {
        return status;
    }

    public PasscodeModel getData() {
        return data;
    }
}
