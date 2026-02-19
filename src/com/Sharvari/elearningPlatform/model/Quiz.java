package com.Sharvari.elearningPlatform.model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private String quizId;
    private String title;
    private String courseId;
    private List<Question> questions;
    private int timeLimitMinutes;
    private double passingScore;

    public Quiz(String quizId, String title, String courseId, int timeLimitMinutes, double passingScore) {
        this.quizId = quizId;
        this.title = title;
        this.courseId = courseId;
        this.timeLimitMinutes = timeLimitMinutes;
        this.passingScore = passingScore;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question)    { questions.add(question); }
    public void removeQuestion(String questionId) { questions.removeIf(q -> q.getQuestionId().equals(questionId)); }

    public int getTotalMarks() {
        int total = 0;
        for (Question q : questions) total += q.getMarks();
        return total;
    }

    public boolean isPassed(double scorePercent) { return scorePercent >= passingScore; }

    public String getQuizId()              { return quizId; }
    public String getTitle()               { return title; }
    public String getCourseId()            { return courseId; }
    public List<Question> getQuestions()   { return questions; }
    public int getTimeLimitMinutes()       { return timeLimitMinutes; }
    public double getPassingScore()        { return passingScore; }

    public void setTitle(String title)               { this.title = title; }
    public void setTimeLimitMinutes(int t)           { this.timeLimitMinutes = t; }
    public void setPassingScore(double passingScore) { this.passingScore = passingScore; }

    @Override
    public String toString() {
        return String.format("Quiz ID: %-8s | %-25s | Questions: %d | Total Marks: %d | Pass: %.0f%% | Time: %d min",
                quizId, title, questions.size(), getTotalMarks(), passingScore, timeLimitMinutes);
    }
}
