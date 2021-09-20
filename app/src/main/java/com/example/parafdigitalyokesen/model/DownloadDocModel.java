package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class DownloadDocModel {
    @SerializedName("file")
    String link;

    public String getLink() {
        return link;
    }
}
