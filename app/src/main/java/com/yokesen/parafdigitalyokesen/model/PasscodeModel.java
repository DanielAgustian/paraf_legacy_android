package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class PasscodeModel {
    @SerializedName("passcode")
    String passcode;

    @SerializedName("is_active")
    int isActive;


    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
