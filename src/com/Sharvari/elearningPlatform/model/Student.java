package com.Sharvari.elearningPlatform.model;



public class Student extends User {

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password, "STUDENT");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Welcome Student Dashboard");
    }
}

