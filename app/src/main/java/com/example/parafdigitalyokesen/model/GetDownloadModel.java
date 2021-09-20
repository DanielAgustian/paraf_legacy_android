package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetDownloadModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private DownloadDocModel data;

    public String getStatus() {
        return status;
    }

    public DownloadDocModel getData() {
        return data;
    }
}
