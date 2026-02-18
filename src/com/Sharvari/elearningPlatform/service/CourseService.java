package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.model.Course;
import java.util.List;

public interface CourseService {

    void createCourse(Course course);
    List<Course> getAllCourses();
}