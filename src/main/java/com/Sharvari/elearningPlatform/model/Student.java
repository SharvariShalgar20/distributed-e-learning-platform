package com.Sharvari.elearningPlatform.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private List<String> enrolledCourseIds;
    private double overallProgress;

    public Student(String userId, String name, String email, String password) {
        super(userId, name, email, password, "STUDENT");
        this.enrolledCourseIds = new ArrayList<>();
        this.overallProgress = 0.0;
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║        STUDENT DASHBOARD         ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("  Welcome, " + getName() + "!");
        System.out.println("  Enrolled Courses : " + enrolledCourseIds.size());
        System.out.println("  Overall Progress : " + String.format("%.1f", overallProgress) + "%");
        System.out.println("──────────────────────────────────");
    }


    public void enrollCourse(String courseId) {
        if (!enrolledCourseIds.contains(courseId)) enrolledCourseIds.add(courseId);
    }


    public void unenrollCourse(String courseId) {
        enrolledCourseIds.remove(courseId);
    }

    public boolean isEnrolled(String courseId) {
        return enrolledCourseIds.contains(courseId);
    }

    public List<String> getEnrolledCourseIds()             { return enrolledCourseIds; }

    public double getOverallProgress()                      { return overallProgress; }

    public void setOverallProgress(double overallProgress)  { this.overallProgress = overallProgress; }
}

