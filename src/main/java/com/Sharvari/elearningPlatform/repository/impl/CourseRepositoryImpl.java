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

    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course(
                rs.getString("course_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("instructor_id"),
                rs.getString("category"),
                rs.getInt("duration_hours")
        );
        if (rs.getBoolean("is_published")) {
            course.publish();
        }
        // quiz IDs and enrolled student IDs are managed by their own tables;
        // they are re-populated by the service layer on startup if needed.
        return course;
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

    @Override
    public Optional<Course> findById(String courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(Course) failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Course> findAll() {
        return queryList("SELECT * FROM courses", ps -> {});
    }

    @Override
    public List<Course> findByInstructorId(String instructorId) {
        return queryList(
                "SELECT * FROM courses WHERE instructor_id = ?",
                ps -> ps.setString(1, instructorId)
        );
    }

    @Override
    public List<Course> findPublished() {
        return queryList(
                "SELECT * FROM courses WHERE is_published = 1",
                ps -> {}
        );
    }

    @FunctionalInterface
    private interface Setter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<Course> queryList(String sql, Setter setter) {
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("queryList(Course) failed: " + e.getMessage(), e);
        }
        return list;
    }

}
