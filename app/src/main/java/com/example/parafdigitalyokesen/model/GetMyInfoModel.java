package com.example.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class GetMyInfoModel {
    @SerializedName("status")
    private String success;

    @SerializedName("data")
    private MyInformationModel myInfo;

    public GetMyInfoModel(String success, MyInformationModel myInfo) {
        this.success = success;
        this.myInfo = myInfo;
    }



    public String getSuccess() {
        return success;
    }

    public MyInformationModel getHome() {
        return myInfo;
    }
}
