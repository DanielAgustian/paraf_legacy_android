package com.example.parafdigitalyokesen.model;

import android.app.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationModel {
    int id;
    String title;
    String details;

    public NotificationModel(int id, String title, String details) {
        this.id = id;
        this.title = title;
        this.details = details;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public static List<NotificationModel> generateModel(int size){
        List<NotificationModel> contacts = new ArrayList<NotificationModel>();
        for (int i =0; i< size; i++){
            contacts.add(new
                    NotificationModel(
                            i, "Lorem Ipsum", "<b>Lorem Ipsum </b>"+" do si doro amaet"
                    )
            );
        }
        return contacts;
    }
}
