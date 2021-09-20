package com.example.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

public class CollabViewModel {

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("email")
    private String email;

    @SerializedName("initiated_on")
    private String inititated;

    @SerializedName("qr_code")
    private String qrCode;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInititated() {
        return inititated;
    }

    public void setInititated(String inititated) {
        this.inititated = inititated;
    }

    public String getQrCode() {
        byte[] data = Base64.decode(qrCode, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);
        return text;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
