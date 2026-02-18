package com.Sharvari.elearningPlatform.service.impl;

import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.service.CourseService;

import java.util.ArrayList;
import java.util.List;

public class CourseServiceImpl implements CourseService {

    private List<Course> courseList = new ArrayList<>();

    @Override
    public void createCourse(Course course) {
        courseList.add(course);
        System.out.println("Course created successfully!");
    }

    @Override
    public List<Course> getAllCourses() {
        return courseList;
    }
}
