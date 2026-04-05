package com.Sharvari.elearningPlatform.model;
import java.time.LocalDate;

public class Enrollment {

    private String enrollmentId;
    private String studentId;
    private String courseId;
    private LocalDate enrollmentDate;
    private double progress;
    private String status; // ACTIVE, COMPLETED, DROPPED

    public Enrollment(String enrollmentId, String studentId, String courseId) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = LocalDate.now();
        this.progress = 0.0;
        this.status = "ACTIVE";
    }

    public void updateProgress(double progress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        this.progress = progress;
        if (this.progress == 100.0) this.status = "COMPLETED";
    }

    public void drop() { this.status = "DROPPED"; }

    public String getEnrollmentId()      { return enrollmentId; }
    public String getStudentId()         { return studentId; }
    public String getCourseId()          { return courseId; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public double getProgress()          { return progress; }
    public String getStatus()            { return status; }

    @Override
    public String toString() {
        return String.format("Enrollment: %s | Student: %s | Course: %s | Progress: %.1f%% | Status: %s | Date: %s",
                enrollmentId, studentId, courseId, progress, status, enrollmentDate);
    }
}
