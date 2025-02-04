package models;

public class Message {
    String content;
    Role role;

    public Message() {
    }

    public Message(String content, Role role) {
        this.content = content;
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public Role getRole() {
        return role;
    }
}
