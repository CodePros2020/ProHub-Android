package com.codepros.prohub.model;

public class Chat {

    private String id;
    private String phoneNumber;
    private String message;
    private String fullName;
    private String photoUrl;
    private String imageUrl;

    public Chat() {
    }

    public Chat(String phoneNumber, String message, String fullName, String photoUrl, String imageUrl) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
