package com.internship.system.model.user;

public abstract class User {
    private final String userId;
    private final String name;
    private String password;

    protected User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
