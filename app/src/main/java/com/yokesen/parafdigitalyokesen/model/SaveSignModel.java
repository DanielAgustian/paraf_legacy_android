package com.yokesen.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

public class SaveSignModel {
    @SerializedName("qr_code")
    private String qr_code;

    public String getQr_code() {
        byte[] data = Base64.decode(qr_code, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);
        return text;
    }
}
