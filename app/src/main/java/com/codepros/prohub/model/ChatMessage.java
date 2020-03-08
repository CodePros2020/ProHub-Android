package com.codepros.prohub.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {

    private String chatMessageId;
    private String receiverNumber;
    private String senderNumber;
    private String senderName;

    public ChatMessage() {
    }

    public ChatMessage(String chatMessageId, String receiverNumber, String senderNumber, String senderName) {
        this.chatMessageId = chatMessageId;
        this.receiverNumber = receiverNumber;
        this.senderNumber = senderNumber;
        this.senderName = senderName;
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
}
