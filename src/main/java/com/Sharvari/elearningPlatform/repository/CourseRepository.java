package com.Sharvari.elearningPlatform.repository;

import com.Sharvari.elearningPlatform.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    void save(Course course);
    Optional<Course> findById(String courseId);
    List<Course> findAll();
    List<Course> findByInstructorId(String instructorId);
    List<Course> findPublished();
    void update(Course course);
    void delete(String courseId);

}
