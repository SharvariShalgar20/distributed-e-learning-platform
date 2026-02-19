package com.Sharvari.elearningPlatform.model;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private String category;
    private int durationHours;
    private boolean isPublished;
    private List<String> quizIds;
    private List<String> enrolledStudentIds;


    public Course(String courseId, String title, String description,
                  String instructorId, String category, int durationHours) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.category = category;
        this.durationHours = durationHours;
        this.isPublished = false;
        this.quizIds = new ArrayList<>();
        this.enrolledStudentIds = new ArrayList<>();
    }

    public void addQuiz(String quizId)            { quizIds.add(quizId); }
    public void removeQuiz(String quizId)         { quizIds.remove(quizId); }
    public void enrollStudent(String studentId)   { if (!enrolledStudentIds.contains(studentId)) enrolledStudentIds.add(studentId); }
    public void unenrollStudent(String studentId) { enrolledStudentIds.remove(studentId); }
    public boolean isStudentEnrolled(String sid)  { return enrolledStudentIds.contains(sid); }
    public void publish()                         { this.isPublished = true; }
    public void unpublish()                       { this.isPublished = false; }

    public String getCourseId()                     { return courseId; }
    public String getTitle()                        { return title; }
    public String getDescription()                  { return description; }
    public String getInstructorId()                 { return instructorId; }
    public String getCategory()                     { return category; }
    public int getDurationHours()                   { return durationHours; }
    public boolean isPublished()                    { return isPublished; }
    public List<String> getQuizIds()                { return quizIds; }
    public List<String> getEnrolledStudentIds()     { return enrolledStudentIds; }

    public void setTitle(String title)              { this.title = title; }
    public void setDescription(String description)  { this.description = description; }
    public void setCategory(String category)        { this.category = category; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }


    @Override
    public String toString() {
        return String.format("ID: %-8s | %-30s | Category: %-15s | Duration: %dh | Students: %d | %s",
                courseId, title, category, durationHours,
                enrolledStudentIds.size(), isPublished ? "Published" : "Draft");
    }


}
