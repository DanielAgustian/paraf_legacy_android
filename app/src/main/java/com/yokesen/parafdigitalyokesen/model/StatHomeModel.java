package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class StatHomeModel {
    @SerializedName("today")
    String today;

    @SerializedName("accepted")
    String accepted;

    @SerializedName("rejected")
    String rejected;

    @SerializedName("request")
    String request;

    public StatHomeModel(String today, String accepted, String rejected, String request) {
        this.today = today;
        this.accepted = accepted;
        this.rejected = rejected;
        this.request = request;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getRejected() {
        return rejected;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
