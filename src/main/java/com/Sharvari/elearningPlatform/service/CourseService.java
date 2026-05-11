package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.exception.CourseNotFoundException;
import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.model.Instructor;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.util.IdGenerator;
import com.Sharvari.elearningPlatform.util.InputValidator;
import com.Sharvari.elearningPlatform.repository.impl.CourseRepositoryImpl;

import java.util.*;

public class CourseService {

    private final Map<String, Course> coursesById = new HashMap<>();

    private final CourseRepositoryImpl courseRepository;
    private final UserService userService;

    public CourseService(CourseRepositoryImpl courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    // ── Create ───────────────────────────────────────────────────────────────

    public Course createCourse(String instructorId, String title, String description,
                               String category, int durationHours) {
        User user = userService.findById(instructorId);
        if (!(user instanceof Instructor)) throw new IllegalArgumentException("Only instructors can create courses.");
        if (!InputValidator.isNotBlank(title))            throw new IllegalArgumentException("Title cannot be blank.");
        if (!InputValidator.isNotBlank(description))      throw new IllegalArgumentException("Description cannot be blank.");
        if (!InputValidator.isNotBlank(category))         throw new IllegalArgumentException("Category cannot be blank.");
        if (!InputValidator.isPositiveInt(durationHours)) throw new IllegalArgumentException("Duration must be positive.");

        String id = IdGenerator.generateCourseId();
        Course course = new Course(id, title, description, instructorId, category, durationHours);

        courseRepository.save(course);

        System.out.println("  ✔ Course created! ID: " + id + " | " + title);
        return course;
    }

    // ── Publish / Unpublish ──────────────────────────────────────────────────

    public void publishCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        course.publish();
        courseRepository.update(course);
        System.out.println("  ✔ Course published: " + course.getTitle());
    }

    public void unpublishCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        course.unpublish();
        courseRepository.update(course);
        System.out.println("  ✔ Course set to Draft: " + course.getTitle());
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public Course findById(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + courseId));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findPublished();
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> searchCoursesByTitle(String keyword) {
        return courseRepository.findPublishedByTitleKeyword(keyword);
    }

    public List<Course> searchCoursesByCategory(String category) {
        return courseRepository.findPublishedByCategory(category);
    }

    // ── Update ───────────────────────────────────────────────────────────────

    public void updateCourse(String instructorId, String courseId, String title,
                             String description, String category, int durationHours) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        if (InputValidator.isNotBlank(title))       course.setTitle(title);
        if (InputValidator.isNotBlank(description)) course.setDescription(description);
        if (InputValidator.isNotBlank(category))    course.setCategory(category);
        if (durationHours > 0)                      course.setDurationHours(durationHours);

        courseRepository.update(course);

        System.out.println("  ✔ Course updated: " + courseId);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    public void deleteCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);

        // FK CASCADE removes quizzes, enrollments
        courseRepository.delete(courseId);

        System.out.println("  ✔ Course deleted: " + courseId);
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    private void verifyOwnership(String instructorId, Course course) {
        if (!course.getInstructorId().equals(instructorId))
            throw new SecurityException("You do not own this course.");
    }

}
