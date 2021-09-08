package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class SimpleResponse {
    @SerializedName("status")
    String status;

    @SerializedName("messages")
    String messages;

    public SimpleResponse(String status, String messages) {
        this.status = status;
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public String getMessages() {
        return messages;
    }
}

