package com.example.parafdigitalyokesen.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SignersModel {
    private String name;
    private String email;

    public SignersModel(String name, String email) {
        this.name = name;
        this.email = email;
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
    public static List<SignersModel> generateModel( int num){
        List<SignersModel> model = new ArrayList<SignersModel>();
        for(int i = 1; i< num+1 ; i++){
            model.add(new SignersModel(
                "Person"+i, "email"+1+"@gmail.com"
            ));
        }
        return model;
    }
}
