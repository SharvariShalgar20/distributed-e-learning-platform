package com.Sharvari.elearningPlatform.repository.impl;

import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.repository.CourseRepository;
import com.Sharvari.elearningPlatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepositoryImpl {

    private Connection conn() implements CourseRepository{
        return DBConnection.getConnection();
    }


    @Override
    public void save(Course course) {
        String sql = """
                INSERT INTO courses
                    (course_id, title, description, instructor_id,
                     category, duration_hours, is_published)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, course.getCourseId());
            ps.setString(2, course.getTitle());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getInstructorId());
            ps.setString(5, course.getCategory());
            ps.setInt   (6, course.getDurationHours());
            ps.setBoolean(7, course.isPublished());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("save(Course) failed: " + e.getMessage(), e);
        }
    }



}
