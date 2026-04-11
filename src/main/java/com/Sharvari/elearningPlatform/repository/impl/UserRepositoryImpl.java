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

    private User mapRow(ResultSet rs) throws SQLException {
        String userId   = rs.getString("user_id");
        String name     = rs.getString("name");
        String email    = rs.getString("email");
        String password = rs.getString("password");
        String role     = rs.getString("role");

        if ("INSTRUCTOR".equals(role)) {
            String expertise = rs.getString("expertise");
            Instructor inst  = new Instructor(userId, name, email, password,
                    expertise == null ? "" : expertise);
            // Re-hydrate created course ids from the courses table on demand
            // (loaded lazily via CourseService.getCoursesByInstructor)
            return inst;
        } else {
            Student student = new Student(userId, name, email, password);
            double overall  = rs.getDouble("overall_progress");
            student.setOverallProgress(overall);
            // Enrolled course ids are loaded lazily via EnrollmentService
            return student;
        }
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


    @Override
    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(User) failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmail failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll(User) failed: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(User user) {
        String sql = """
                UPDATE users
                   SET name = ?, email = ?, password = ?,
                       expertise = ?, overall_progress = ?
                 WHERE user_id = ?
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            if (user instanceof Instructor) {
                ps.setString(4, ((Instructor) user).getExpertise());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            double progress = (user instanceof Student)
                    ? ((Student) user).getOverallProgress() : 0.0;
            ps.setDouble(5, progress);
            ps.setString(6, user.getUserId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update(User) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete(User) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByEmail failed: " + e.getMessage(), e);
        }
    }

}
