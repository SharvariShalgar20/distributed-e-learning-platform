package com.Sharvari.elearningPlatform.repository.impl;

import com.Sharvari.elearningPlatform.model.Enrollment;
import com.Sharvari.elearningPlatform.repository.EnrollmentRepository;
import com.Sharvari.elearningPlatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment(
                rs.getString("enrollment_id"),
                rs.getString("student_id"),
                rs.getString("course_id")
        );
        // Override the default values set in the constructor with DB values
        double progress = rs.getDouble("progress");
        // updateProgress also adjusts status, so we use a small workaround:
        // call the method only when progress differs from 0.
        if (progress > 0) {
            e.updateProgress(progress);
        }
        String status = rs.getString("status");
        // If DB says DROPPED but progress didn't trigger it, force-drop
        if ("DROPPED".equals(status) && !"DROPPED".equals(e.getStatus())) {
            e.drop();
        }
        return e;
    }

    @Override
    public void save(Enrollment enrollment) {
        String sql = """
                INSERT INTO enrollments
                    (enrollment_id, student_id, course_id,
                     enrollment_date, progress, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, enrollment.getEnrollmentId());
            ps.setString(2, enrollment.getStudentId());
            ps.setString(3, enrollment.getCourseId());
            ps.setDate  (4, Date.valueOf(enrollment.getEnrollmentDate()));
            ps.setDouble(5, enrollment.getProgress());
            ps.setString(6, enrollment.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("save(Enrollment) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Enrollment> findById(String enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(Enrollment) failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId) {
        String sql = """
                SELECT * FROM enrollments
                 WHERE student_id = ? AND course_id = ? AND status != 'DROPPED'
                 LIMIT 1
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByStudentAndCourse failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
