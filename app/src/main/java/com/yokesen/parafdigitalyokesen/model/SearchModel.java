package com.yokesen.parafdigitalyokesen.model;

import java.util.List;

public class SearchModel {

    int type;

    List<SignModel> sign;



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SignModel> getSign() {
        return sign;
    }

    public void setSign(List<SignModel> sign) {
        this.sign = sign;
    }
}
