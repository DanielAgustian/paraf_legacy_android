package com.example.parafdigitalyokesen.model;

import android.app.Notification;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NotificationModel {
    @SerializedName("id")
    int id;

    @SerializedName("collaboration_id")
    int collabId;

    @SerializedName("mesage")
    String title;

    @SerializedName("information")
    String details;

    @SerializedName("name")
    String sender;

    @SerializedName("status")
    String unread;

    @SerializedName("time")
    String time;

    public NotificationModel( String title, String details) {

        this.title = title;
        this.details = details;
    }

    public int getId() {
        return id;
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
                             "Lorem Ipsum", "<b>Lorem Ipsum </b>"+" do si doro amaet"
                    )
            );
        }
        return contacts;
    }

    public String getSender() {
        return sender;
    }

    public String getUnread() {
        return unread;
    }

    public String getTime() {
        return time;
    }

    public int getCollabId() {
        return collabId;
    }
}
