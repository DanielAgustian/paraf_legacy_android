package com.yokesen.parafdigitalyokesen.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTypeCategoryModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<TypeCategoryModel> data;

    public GetTypeCategoryModel(String success, List<TypeCategoryModel> data) {
        this.status = success;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public List<TypeCategoryModel> getData() {
        return data;
    }
}
