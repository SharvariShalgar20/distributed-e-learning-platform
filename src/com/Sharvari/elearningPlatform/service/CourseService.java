package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.exception.CourseNotFoundException;
import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.model.Instructor;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.util.IdGenerator;
import com.Sharvari.elearningPlatform.util.InputValidator;

import java.util.*;

public class CourseService {

    private final Map<String, Course> coursesById = new HashMap<>();
    private final UserService userService;

    public CourseService(UserService userService) {
        this.userService = userService;
    }

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
        coursesById.put(id, course);
        ((Instructor) user).addCourse(id);
        System.out.println("  ✔ Course created! ID: " + id + " | " + title);
        return course;
    }


    public void publishCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        course.publish();
        System.out.println("  ✔ Course published: " + course.getTitle());
    }

    public void unpublishCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        course.unpublish();
        System.out.println("  ✔ Course set to Draft: " + course.getTitle());
    }

    public Course findById(String courseId) {
        Course course = coursesById.get(courseId);
        if (course == null) throw new CourseNotFoundException("Course not found: " + courseId);
        return course;
    }


    private void verifyOwnership(String instructorId, Course course) {
        if (!course.getInstructorId().equals(instructorId))
            throw new SecurityException("You do not own this course.");
    }

    public void updateCourse(String instructorId, String courseId, String title,
                             String description, String category, int durationHours) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        if (InputValidator.isNotBlank(title))       course.setTitle(title);
        if (InputValidator.isNotBlank(description)) course.setDescription(description);
        if (InputValidator.isNotBlank(category))    course.setCategory(category);
        if (durationHours > 0)                      course.setDurationHours(durationHours);
        System.out.println("  ✔ Course updated: " + courseId);
    }

    public void deleteCourse(String instructorId, String courseId) {
        Course course = findById(courseId);
        verifyOwnership(instructorId, course);
        coursesById.remove(courseId);
        ((Instructor) userService.findById(instructorId)).removeCourse(courseId);
        System.out.println("  ✔ Course deleted: " + courseId);
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(coursesById.values());
    }

    public List<Course> getPublishedCourses() {
        List<Course> list = new ArrayList<>();
        for (Course c : coursesById.values()) if (c.isPublished()) list.add(c);
        return list;
    }

    public List<Course> getCoursesByInstructor(String instructorId) {
        List<Course> list = new ArrayList<>();
        for (Course c : coursesById.values()) if (c.getInstructorId().equals(instructorId)) list.add(c);
        return list;
    }

    public List<Course> searchCoursesByTitle(String keyword) {
        List<Course> list = new ArrayList<>();
        for (Course c : coursesById.values())
            if (c.isPublished() && c.getTitle().toLowerCase().contains(keyword.toLowerCase())) list.add(c);
        return list;
    }

    public List<Course> searchCoursesByCategory(String category) {
        List<Course> list = new ArrayList<>();
        for (Course c : coursesById.values())
            if (c.isPublished() && c.getCategory().equalsIgnoreCase(category)) list.add(c);
        return list;
    }

    public void loadDemoData(String drSarahId, String profMarkId) {
        Course c1 = createCourse(drSarahId, "Java Programming Fundamentals",
                "Learn Java from scratch with OOP principles.", "Programming", 20);
        publishCourse(drSarahId, c1.getCourseId());

        Course c2 = createCourse(drSarahId, "Data Structures & Algorithms",
                "Master DSA for coding interviews.", "Programming", 30);
        publishCourse(drSarahId, c2.getCourseId());

        Course c3 = createCourse(profMarkId, "Python for Data Science",
                "Hands-on data science with Python and pandas.", "Data Science", 25);
        publishCourse(profMarkId, c3.getCourseId());

        Course c4 = createCourse(profMarkId, "Machine Learning Basics",
                "Introduction to ML algorithms and concepts.", "Data Science", 35);
        publishCourse(profMarkId, c4.getCourseId());
    }

}
