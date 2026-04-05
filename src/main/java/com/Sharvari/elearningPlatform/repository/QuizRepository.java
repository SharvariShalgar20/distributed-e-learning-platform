package com.Sharvari.elearningPlatform.repository;

import com.Sharvari.elearningPlatform.model.Quiz;
import java.util.List;
import java.util.Optional;

public interface QuizRepository {

    void save(Quiz quiz);
    Optional<Quiz> findById(String quizId);
    List<Quiz> findByCourseId(String courseId);
    List<Quiz> findAll();
    void update(Quiz quiz);
    void delete(String quizId);
}
