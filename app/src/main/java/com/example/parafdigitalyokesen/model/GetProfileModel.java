package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetProfileModel {
    @SerializedName("status")
    private String success;

    @SerializedName("data")
    private ProfileModel myInfo;

    public GetProfileModel(String success, ProfileModel myInfo) {
        this.success = success;
        this.myInfo = myInfo;
    }



    public String getSuccess() {
        return success;
    }

    public ProfileModel getHome() {
        return myInfo;
    }
}
