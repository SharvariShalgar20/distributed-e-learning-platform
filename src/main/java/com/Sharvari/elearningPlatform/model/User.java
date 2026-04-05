package com.Sharvari.elearningPlatform.model;

public abstract class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private String role;

    public User(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public abstract void displayDashboard();

    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Email: %s | Role: %s", userId, name, email, role);
    }
}
