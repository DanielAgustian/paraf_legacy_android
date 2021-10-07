package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class CollabCounterModel {

    @SerializedName("requested")
    int requested;

    @SerializedName("accepted")
    int accepted;

    @SerializedName("rejected")
    int rejected;


    public int getRequested() {
        return requested;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getRejected() {
        return rejected;
    }
}
