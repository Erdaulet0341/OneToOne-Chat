package com.onetoonechat;

public class FirebaseModel {
    String name;
    String image;
    String uid;
    String status;
    String token_notification;

    public FirebaseModel() {
    }

    public FirebaseModel(String name, String image, String uid, String status, String token_notification) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
        this.token_notification = token_notification;
    }

    public String getToken_notification() {
        return token_notification;
    }

    public void setToken_notification(String token_notification) {
        this.token_notification = token_notification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
