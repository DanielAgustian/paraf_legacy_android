package com.example.parafdigitalyokesen.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SignersModel implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("photo")
    private String photo;

    @SerializedName("status")
    private String status;

    @SerializedName("information")
    private String info;



    public SignersModel(String name, String email, String photo, String status, String info) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.status = status;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static List<SignersModel> generateModel( int num){
        List<SignersModel> model = new ArrayList<SignersModel>();
        for(int i = 1; i< num+1 ; i++){
            model.add(new SignersModel(
                "Person"+i, "email"+1+"@gmail.com", "", "waiting", "waiting for signature"
            ));
        }
        return model;
    }
}
