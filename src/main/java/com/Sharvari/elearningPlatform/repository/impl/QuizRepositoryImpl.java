package com.Sharvari.elearningPlatform.repository.impl;

import com.Sharvari.elearningPlatform.model.Question;
import com.Sharvari.elearningPlatform.model.Quiz;
import com.Sharvari.elearningPlatform.repository.QuizRepository;
import com.Sharvari.elearningPlatform.util.DBConnection;

import java.sql.*;
import java.util.*;

public class QuizRepositoryImpl implements QuizRepository {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    // ── CRUD: quizzes ────────────────────────────────────────────────────────

    @Override
    public void save(Quiz quiz) {
        String sql = """
                INSERT INTO quizzes
                    (quiz_id, title, course_id, time_limit_minutes, passing_score)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, quiz.getQuizId());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getCourseId());
            ps.setInt   (4, quiz.getTimeLimitMinutes());
            ps.setDouble(5, quiz.getPassingScore());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("save(Quiz) failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Quiz quiz) {
        String sql = """
                UPDATE quizzes
                   SET title = ?, time_limit_minutes = ?, passing_score = ?
                 WHERE quiz_id = ?
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setInt   (2, quiz.getTimeLimitMinutes());
            ps.setDouble(3, quiz.getPassingScore());
            ps.setString(4, quiz.getQuizId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update(Quiz) failed: " + e.getMessage(), e);
        }
    }


}
