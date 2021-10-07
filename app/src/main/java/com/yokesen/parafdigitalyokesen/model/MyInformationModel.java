package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;


public class MyInformationModel {
    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("phone")
    String phone;


    @SerializedName("photo")
    String photo;

    @SerializedName("initial")
    String initial;

    @SerializedName("position")
    String position;

    @SerializedName("company")
    String company;

    public MyInformationModel(String name, String email, String phone, String initial, String position, String company) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.initial = initial;
        this.position = position;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getInitial() {
        return initial;
    }

    public String getPosition() {
        return position;
    }

    public String getCompany() {
        return company;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhoto() {
        return photo;
    }
}
