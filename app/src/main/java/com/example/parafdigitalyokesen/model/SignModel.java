package com.example.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SignModel {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String title;

    @SerializedName("created_at")
    private String time;

    @SerializedName("qr_code")
    private String qr_code;


    public SignModel(String title, String time, int id) {
        this.title = title;
        this.time = time;
        this.id = id;
    }

    public String getQr_code() {
        byte[] data = Base64.decode(qr_code, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);
        return text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public static List<SignModel> generateList(){
        List<SignModel> model = new ArrayList<SignModel>();
        for (int i=0; i< 3; i++){
           model.add(new SignModel(
                "Title i", "12/05/21", 2
           ));
        }
        return model;
    }
}
