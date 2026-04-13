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


}
