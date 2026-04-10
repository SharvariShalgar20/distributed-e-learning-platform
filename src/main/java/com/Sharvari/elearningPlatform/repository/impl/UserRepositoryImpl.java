package com.Sharvari.elearningPlatform.repository.impl;

import com.Sharvari.elearningPlatform.model.Instructor;
import com.Sharvari.elearningPlatform.model.Student;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.repository.UserRepository;
import com.Sharvari.elearningPlatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    @Override
    public void save(User user) {
        String sql = """
                INSERT INTO users
                    (user_id, name, email, password, role, expertise, overall_progress)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());

            if (user instanceof Instructor) {
                ps.setString(6, ((Instructor) user).getExpertise());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            double progress = (user instanceof Student)
                    ? ((Student) user).getOverallProgress() : 0.0;
            ps.setDouble(7, progress);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("save(User) failed: " + e.getMessage(), e);
        }
    }



}
