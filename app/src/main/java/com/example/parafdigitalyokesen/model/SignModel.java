package com.example.parafdigitalyokesen.model;

import java.util.ArrayList;
import java.util.List;

public class SignModel {
    private String title, time;
    private int id;

    public SignModel(String title, String time, int id) {
        this.title = title;
        this.time = time;
        this.id = id;
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
