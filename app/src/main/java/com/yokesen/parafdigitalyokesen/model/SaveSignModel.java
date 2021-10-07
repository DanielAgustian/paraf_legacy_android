package com.yokesen.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

public class SaveSignModel {
    @SerializedName("qr_code")
    private String qr_code;

    public String getQr_code() {

        return qr_code;
    }
}
