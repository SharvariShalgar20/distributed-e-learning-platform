package com.Sharvari.elearningPlatform.model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {

    private String expertise;
    private List<String> createdCourseIds;

    public Instructor(String userId, String name, String email, String password, String expertise) {
        super(userId, name, email, password, "INSTRUCTOR");
        this.expertise = expertise;
        this.createdCourseIds = new ArrayList<>();
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║      INSTRUCTOR DASHBOARD        ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("  Welcome, " + getName() + "!");
        System.out.println("  Expertise      : " + expertise);
        System.out.println("  Courses Created: " + createdCourseIds.size());
        System.out.println("──────────────────────────────────");
    }

    public void addCourse(String courseId) {
        if (!createdCourseIds.contains(courseId)) createdCourseIds.add(courseId);
    }

    public void removeCourse(String courseId) { createdCourseIds.remove(courseId); }

    public List<String> getCreatedCourseIds() { return createdCourseIds; }
    public String getExpertise()               { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
}
