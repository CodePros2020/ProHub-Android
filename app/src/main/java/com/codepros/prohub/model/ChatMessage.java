package com.codepros.prohub.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {

    private String chatMessageId;
    private String receiverNumber;
    private String senderPhotoUrl;
    private String senderNumber;
    private String senderName;
    private String timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String chatMessageId, String receiverNumber, String senderPhotoUrl, String senderNumber, String senderName, String timestamp) {
        this.chatMessageId = chatMessageId;
        this.receiverNumber = receiverNumber;
        this.senderPhotoUrl = senderPhotoUrl;
        this.senderNumber = senderNumber;
        this.senderName = senderName;
        this.timestamp = timestamp;
    }

    public String getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(String chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
