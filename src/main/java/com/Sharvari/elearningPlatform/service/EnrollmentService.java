package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.model.Enrollment;
import com.Sharvari.elearningPlatform.model.Student;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.repository.impl.EnrollmentRepositoryImpl;
import com.Sharvari.elearningPlatform.util.IdGenerator;

import java.util.*;

public class EnrollmentService {

    private final Map<String, Enrollment> enrollmentsById = new HashMap<>();

    private final EnrollmentRepositoryImpl enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepositoryImpl enrollmentRepository, UserService userService, CourseService courseService) {

        this.enrollmentRepository = enrollmentRepository;
        this.userService   = userService;
        this.courseService = courseService;
    }

    // ── Enroll ───────────────────────────────────────────────────────────

    public Enrollment enrollStudent(String studentId, String courseId) {
        User user = userService.findById(studentId);
        if (!(user instanceof Student)) throw new IllegalArgumentException("Only students can enroll.");

        Course course = courseService.findById(courseId);
        if (!course.isPublished()) throw new IllegalStateException("Course is not published yet.");

        if (findByStudentAndCourse(studentId, courseId).isPresent())
            throw new IllegalStateException("Already enrolled in this course.");

        String id = IdGenerator.generateEnrollmentId();
        Enrollment enrollment = new Enrollment(id, studentId, courseId);

        enrollmentRepository.save(enrollment);

        ((Student) user).enrollCourse(courseId);
        course.enrollStudent(studentId);

        System.out.println("  ✔ Enrolled successfully! ID: " + id + " | Course: " + course.getTitle());

        return enrollment;
    }

    // ── Drop ────────────────────────────────────────────────────────────

    public void dropCourse(String studentId, String courseId) {
        Enrollment enrollment = findByStudentAndCourse(studentId, courseId)
                .orElseThrow(() -> new IllegalStateException("Enrollment not found."));

        enrollment.drop();

        String enrollmentId = enrollment.getEnrollmentId();
        enrollmentRepository.delete(enrollmentId);

        User user = userService.findById(studentId);
        if (user instanceof Student)
            ((Student) user).unenrollCourse(courseId);
        courseService.findById(courseId).unenrollStudent(studentId);

        System.out.println("  ✔ Course dropped: " + courseId);
    }

    // ── Progress ───────────────────────────────────────────────────────────

    public void updateProgress(String studentId, String courseId, double progress) {

        Enrollment enrollment = findByStudentAndCourse(studentId, courseId)
                .orElseThrow(() -> new IllegalStateException("Enrollment not found."));

        enrollment.updateProgress(progress);
        enrollmentRepository.update(enrollment);
        recalculateStudentProgress(studentId);

        System.out.printf("  ✔ Progress updated to %.1f%%%n", progress);

        if (progress == 100.0)
            System.out.println("  🎉 Congratulations! Course completed!");
    }

    private void recalculateStudentProgress(String studentId) {
        List<Enrollment> list = getEnrollmentsByStudent(studentId);

        double total = 0;
        int count = 0;

        for (Enrollment e : list) {
            if (!e.getStatus().equals("DROPPED")) {
                total += e.getProgress();
                count++;
            }
        }

        User user = userService.findById(studentId);
        if (user instanceof Student student && count > 0) {
            student.setOverallProgress(total / count);
            // Persist updated overall progress to DB
            userService.updateOverallProgress(student);

        }
    }


    public Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId) {
        return enrollmentRepository.findByStudentAndCourse(studentId, courseId);
    }


    public List<Enrollment> getEnrollmentsByStudent(String studentId) {
        List<Enrollment> list = new ArrayList<>();
        for (Enrollment e : enrollmentsById.values()) if (e.getStudentId().equals(studentId)) list.add(e);
        return list;
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseId) {
        List<Enrollment> list = new ArrayList<>();
        for (Enrollment e : enrollmentsById.values()) if (e.getCourseId().equals(courseId)) list.add(e);
        return list;
    }

    public void displayStudentProgress(String studentId) {
        List<Enrollment> enrollments = getEnrollmentsByStudent(studentId);
        if (enrollments.isEmpty()) { System.out.println("  No enrollments found."); return; }
        System.out.println("\n  ── Your Course Progress ──────────────────");
        for (Enrollment e : enrollments) {
            String title;
            try { title = courseService.findById(e.getCourseId()).getTitle(); }
            catch (Exception ex) { title = e.getCourseId(); }
            System.out.printf("  %-30s %s %.1f%% [%s]%n",
                    title, buildProgressBar(e.getProgress()), e.getProgress(), e.getStatus());
        }
        System.out.println("  ──────────────────────────────────────────");
    }

    private String buildProgressBar(double progress) {
        int filled = (int)(progress / 10);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(i < filled ? "█" : "░");
        return sb.append("]").toString();
    }




}
