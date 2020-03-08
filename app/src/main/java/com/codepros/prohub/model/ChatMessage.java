package com.codepros.prohub.model;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private String chatId;
    private List<Chat> messages = new ArrayList<>();

    public ChatMessage(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<Chat> getMessages() {
        return messages;
    }

    public void setMessages(List<Chat> messages) {
        this.messages = messages;
    }
}
