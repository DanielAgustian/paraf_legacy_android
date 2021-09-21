package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

public class TypeCategoryModel {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    public TypeCategoryModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
