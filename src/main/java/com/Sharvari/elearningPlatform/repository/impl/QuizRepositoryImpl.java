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

    private Quiz mapQuizRow(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz(
                rs.getString("quiz_id"),
                rs.getString("title"),
                rs.getString("course_id"),
                rs.getInt("time_limit_minutes"),
                rs.getDouble("passing_score")
        );
        // Eagerly load questions for this quiz
        loadQuestionsInto(quiz);
        return quiz;
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
    public Optional<Quiz> findById(String quizId) {
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapQuizRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById(Quiz) failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Quiz> findByCourseId(String courseId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE course_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapQuizRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByCourseId(Quiz) failed: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Quiz> findAll() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quizzes";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapQuizRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll(Quiz) failed: " + e.getMessage(), e);
        }
        return list;
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

    private void loadQuestionsInto(Quiz quiz) {
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, quiz.getQuizId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] options = {
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d")
                    };
                    char correct = rs.getString("correct_answer").charAt(0);
                    Question q = new Question(
                            rs.getString("question_id"),
                            rs.getString("question_text"),
                            options,
                            correct,
                            rs.getInt("marks")
                    );
                    quiz.addQuestion(q);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadQuestionsInto failed: " + e.getMessage(), e);
        }
    }

    // ── Quiz Scores ──────────────────────────────────────────────────────────

    /**
     * Inserts or updates a quiz score (upsert).
     * Uses MySQL's INSERT … ON DUPLICATE KEY UPDATE.
     */

    public void saveOrUpdateScore(String studentId, String quizId, double scorePercent) {
        String sql = """
                INSERT INTO quiz_scores (student_id, quiz_id, score_percent)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE score_percent = VALUES(score_percent),
                                         attempted_at  = CURRENT_TIMESTAMP
                """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, quizId);
            ps.setDouble(3, scorePercent);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveOrUpdateScore failed: " + e.getMessage(), e);
        }
    }


}
