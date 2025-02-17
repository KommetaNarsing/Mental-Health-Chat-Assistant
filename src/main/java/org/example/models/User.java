package org.example.models;

public class User {

    private String  userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }


    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
