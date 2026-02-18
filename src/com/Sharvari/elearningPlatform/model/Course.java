package com.Sharvari.elearningPlatform.model;

public class Course {

    private int courseId;
    private String title;
    private String description;
    private double price;
    private int instructorId;

    public Course(int courseId, String title, String description, double price, int instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.instructorId = instructorId;
    }


    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getInstructorId() { return instructorId; }


}
