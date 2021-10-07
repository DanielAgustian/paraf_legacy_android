package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class SignCounterModel {

    @SerializedName("signature")
    int signature;

    @SerializedName("request")
    int request;

    public int getSignature() {
        return signature;
    }

    public int getRequest() {
        return request;
    }
}
