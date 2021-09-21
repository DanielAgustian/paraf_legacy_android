package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ContactModel {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("photo")
    String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ContactModel(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public static List<ContactModel> generateContact(int size){
        List<ContactModel> contacts = new ArrayList<ContactModel>();
        for (int i =0; i< size; i++){
            contacts.add(new
                    ContactModel(
                         i, "Nanas" +i, "nanas"+i+"@gmail.com"
            )
            );
        }
        return contacts;
    }
}
