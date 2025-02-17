package org.example.models;

public class ChatContent {
    String userId;
    String conversationId;
    String timeStamp;
    String role;
    String content;

    public ChatContent() {

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatContent(String userId, String conversationId, String timeStamp, String role, String content) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.timeStamp = timeStamp;
        this.role = role;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
