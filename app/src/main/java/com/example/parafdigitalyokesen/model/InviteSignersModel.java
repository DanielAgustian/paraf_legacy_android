package com.example.parafdigitalyokesen.model;

import android.widget.EditText;

public class InviteSignersModel {
    private String etName;
    private String etEmail;
    public String getetName() {
        return etName;
    }
    public void setEtName(String editTextValue) {
        this.etName = editTextValue;
    }
    public String getEtEmail() {
        return etEmail;
    }
    public void setEtEmail(String editTextValue) {
        this.etEmail = editTextValue;
    }
}
