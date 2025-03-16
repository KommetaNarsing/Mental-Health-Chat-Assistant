package org.example.models;

public class User {

    private String  userId;
    private String userName;
    private String password;
    private String salt;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public User(String userId, String userName, String password, String salt) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.salt = salt;
    }
}
