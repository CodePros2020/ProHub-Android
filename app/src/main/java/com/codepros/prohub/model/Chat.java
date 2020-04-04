package com.codepros.prohub.model;

import java.text.DateFormat;

public class Chat {

    private String chatId;
    private String chatMessageId;
    private String fullName;
    private String message;
    private String phoneNumber;
    private String photoUrl;
    private String imageUrl;
    private String timestamp;
    private String chatSeen;

    public Chat() {
    }

    public Chat(String chatMessageId, String fullName, String message, String phoneNumber, String photoUrl, String imageUrl, String timestamp, String chatSeen) {
        this.chatMessageId = chatMessageId;
        this.fullName = fullName;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.chatSeen = chatSeen;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(String chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatSeen() {
        return chatSeen;
    }

    public void setChatSeen(String chatSeen) {
        this.chatSeen = chatSeen;
    }
}