package com.example.library.model;

import java.io.Serializable;

public class Account implements Serializable {
    private int userId;
    private String userPassword;
    private String role; // Guest, User, 
    public Account() {}

    public Account(int userId, String userPassword, String role) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}