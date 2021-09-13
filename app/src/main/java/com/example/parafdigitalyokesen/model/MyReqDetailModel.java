package com.example.parafdigitalyokesen.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MyReqDetailModel implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("sign_number")
    private String signNumber;

    @SerializedName("request_by")
    private String requestBy;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("email")
    private String emailReq;

    @SerializedName("name")
    private String title;

    @SerializedName("initiated_on")
    private String initiatedOn;

    @SerializedName("qr_code")
    private String qr_code;

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

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("time")
    private String time;

    @SerializedName("total_signers_waiting")
    private String totalSigners;

    @SerializedName("signers")
    private List<SignersModel> signers;

    @SerializedName("action")
    private boolean action;

    @SerializedName("rejected_reason")
    private String reason;

    public String getCreatedBy() {
        return createdBy;
    }

    public String getReason() {
        return reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(String initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public String getQr_code() {
        byte[] data = Base64.decode(qr_code, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);
        return text;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalSigners() {
        return totalSigners;
    }

    public void setTotalSigners(String totalSigners) {
        this.totalSigners = totalSigners;
    }

    public List<SignersModel> getSigners() {
        return signers;
    }

    public void setSigners(List<SignersModel> signers) {
        this.signers = signers;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public String getEmailReq() {
        return emailReq;
    }

    public void setEmailReq(String emailReq) {
        this.emailReq = emailReq;
    }

    public String getSignNumber() {
        return signNumber;
    }
}
