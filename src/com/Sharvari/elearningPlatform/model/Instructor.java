package com.Sharvari.elearningPlatform.model;

public class Instructor extends User {

    public Instructor(int id, String name, String email, String password) {
        super(id, name, email, password, "INSTRUCTOR");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Welcome Instructor Dashboard");
    }
}
