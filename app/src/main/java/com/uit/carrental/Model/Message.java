package com.uit.carrental.Model;

public class Message {
    private String content;
    private boolean isUser; // true if sent by user, false if by bot

    public Message(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }
}