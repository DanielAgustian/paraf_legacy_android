package com.yokesen.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SignatureDetailModel implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("email")
    private String email;

    @SerializedName("document_name")
    private String documentName;

    @SerializedName("name")
    private String title;

    @SerializedName("created_at")
    private String time;

    @SerializedName("qr_code")
    private String qr_code;

    @SerializedName("initiated_on")
    private String initiatedOn;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("type")
    private String type;

    @SerializedName("link")
    private String link;

    @SerializedName("status")
    private String status;

    @SerializedName("size")
    private String size;

    @SerializedName("sign_number")
    private String signNumber;

    @SerializedName("code")
    private String kode;

    @SerializedName("file")
    private String file;

    @SerializedName("date_created")
    private String dateCreated;

    @SerializedName("time")
    private String timeHour;

    public SignatureDetailModel(String title, String time, int id) {
        this.title = title;
        this.time = time;
        this.id = id;
    }

    public String getQr_code() {
//        byte[] data = Base64.decode(qr_code, Base64.DEFAULT);
//        String text = new String(data, StandardCharsets.UTF_8);
        return qr_code;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getEmail() {
        return email;
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
    public String getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(String initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSignNumber() {
        return signNumber;
    }

    public String getKode() {
        return kode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getFile() {
        return file;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTimeHour() {
        return timeHour;
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
