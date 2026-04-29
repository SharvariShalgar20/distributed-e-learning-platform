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

    @Override
    public void delete(String quizId) {
        // Cascades to questions and quiz_scores via FK ON DELETE CASCADE
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, quizId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete(Quiz) failed: " + e.getMessage(), e);
        }
    }

    // ── Questions ────────────────────────────────────────────────────────────

    public void saveQuestion(Question question, String quizId) {
        String sql = """
                INSERT INTO questions
                    (question_id, quiz_id, question_text,
                     option_a, option_b, option_c, option_d,
                     correct_answer, marks)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, question.getQuestionId());
            ps.setString(2, quizId);
            ps.setString(3, question.getText());
            String[] opts = question.getOptions();
            ps.setString(4, opts[0]);
            ps.setString(5, opts[1]);
            ps.setString(6, opts[2]);
            ps.setString(7, opts[3]);
            ps.setString(8, String.valueOf(question.getCorrectAnswer()));
            ps.setInt   (9, question.getMarks());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveQuestion failed: " + e.getMessage(), e);
        }
    }

    public void deleteQuestion(String questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, questionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteQuestion failed: " + e.getMessage(), e);
        }
    }
}
