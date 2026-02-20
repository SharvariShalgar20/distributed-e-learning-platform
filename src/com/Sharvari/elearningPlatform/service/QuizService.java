package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.model.*;
import com.Sharvari.elearningPlatform.util.IdGenerator;
import com.Sharvari.elearningPlatform.util.InputValidator;

import java.util.*;

public class QuizService {

    private final Map<String, Quiz> quizzesById = new HashMap<>();
    // studentId -> quizId -> score%
    private final Map<String, Map<String, Double>> quizScores = new HashMap<>();
    private final UserService       userService;
    private final CourseService     courseService;
    private final EnrollmentService enrollmentService;

    public QuizService(UserService us, CourseService cs, EnrollmentService es) {
        this.userService       = us;
        this.courseService     = cs;
        this.enrollmentService = es;
    }

    public Quiz createQuiz(String instructorId, String courseId, String title,
                           int timeLimitMinutes, double passingScore) {

        User user = userService.findById(instructorId);
        if (!(user instanceof Instructor)) throw new IllegalArgumentException("Only instructors can create quizzes.");

        Course course = courseService.findById(courseId);
        if (!course.getInstructorId().equals(instructorId)) throw new SecurityException("You do not own this course.");

        if (!InputValidator.isNotBlank(title))              throw new IllegalArgumentException("Quiz title cannot be blank.");
        if (!InputValidator.isValidPercentage(passingScore)) throw new IllegalArgumentException("Passing score must be 0-100.");

        String id = IdGenerator.generateQuizId();
        Quiz quiz = new Quiz(id, title, courseId, timeLimitMinutes, passingScore);
        quizzesById.put(id, quiz);
        course.addQuiz(id);
        System.out.println("  ✔ Quiz created! ID: " + id + " | " + title);
        return quiz;
    }


    public Question addQuestion(String instructorId, String quizId, String text,
                                String[] options, char correctAnswer, int marks) {

        Quiz quiz = findById(quizId);
        Course course = courseService.findById(quiz.getCourseId());

        if (!course.getInstructorId().equals(instructorId)) throw new SecurityException("You do not own this quiz.");
        if (!InputValidator.isNotBlank(text))                throw new IllegalArgumentException("Question text cannot be blank.");
        if (options == null || options.length != 4)          throw new IllegalArgumentException("Must provide exactly 4 options.");
        if (marks <= 0)                                      throw new IllegalArgumentException("Marks must be positive.");

        String qid = IdGenerator.generateQuestionId();
        Question question = new Question(qid, text, options, correctAnswer, marks);
        quiz.addQuestion(question);
        System.out.println("  ✔ Question added! ID: " + qid);
        return question;
    }

    public Quiz findById(String quizId) {
        Quiz quiz = quizzesById.get(quizId);
        if (quiz == null) throw new IllegalArgumentException("Quiz not found: " + quizId);
        return quiz;
    }

    public double attemptQuiz(String studentId, String quizId, Scanner scanner) {
        User user = userService.findById(studentId);
        if (!(user instanceof Student)) throw new IllegalArgumentException("Only students can attempt quizzes.");

        Quiz quiz = findById(quizId);
        Course course = courseService.findById(quiz.getCourseId());
        enrollmentService.findByStudentAndCourse(studentId, quiz.getCourseId())
                .orElseThrow(() -> new IllegalStateException("You must enroll in the course first."));

        List<Question> questions = quiz.getQuestions();
        if (questions.isEmpty()) {
            System.out.println("  ⚠ No questions in this quiz.");
            return 0;
        }

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf( "║  QUIZ: %-35s║%n", quiz.getTitle());
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Course    : " + course.getTitle());
        System.out.println("  Questions : " + questions.size() + " | Total Marks: " + quiz.getTotalMarks());
        System.out.println("  Pass Mark : " + quiz.getPassingScore() + "% | Time: " + quiz.getTimeLimitMinutes() + " min");
        System.out.print("  Press ENTER to start...");
        scanner.nextLine();

        int scored = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            q.display(i + 1);
            char answer = ' ';

            while (!"ABCD".contains(String.valueOf(answer))) {
                System.out.print("  Your answer (A/B/C/D): ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (!input.isEmpty()) answer = input.charAt(0);
                if (!"ABCD".contains(String.valueOf(answer)))
                    System.out.println("  Invalid. Enter A, B, C, or D.");
            }

            if (q.checkAnswer(answer)) {
                System.out.println("  ✔ Correct! +" + q.getMarks() + " marks");
                scored += q.getMarks();
            } else {
                System.out.println("  ✘ Wrong! Correct answer: " + q.getCorrectAnswer());
            }
        }

        double percentage = quiz.getTotalMarks() > 0 ? (scored * 100.0) / quiz.getTotalMarks() : 0.0;

        quizScores.computeIfAbsent(studentId, k -> new HashMap<>()).put(quizId, percentage);

        System.out.println("\n══════════════════════════════════════════");
        System.out.printf("  RESULT: %d / %d marks (%.1f%%)%n", scored, quiz.getTotalMarks(), percentage);
        if (quiz.isPassed(percentage)) System.out.println("  🏆 PASSED! Congratulations!");
        else System.out.printf("  ✘ FAILED. Need %.0f%% to pass. Keep trying!%n", quiz.getPassingScore());
        System.out.println("══════════════════════════════════════════");
        return percentage;
    }

    public List<Quiz> getQuizzesByCourse(String courseId) {
        List<Quiz> list = new ArrayList<>();
        for (Quiz q : quizzesById.values()) if (q.getCourseId().equals(courseId)) list.add(q);
        return list;
    }

    public void displayStudentScores(String studentId) {
        Map<String, Double> scores = quizScores.getOrDefault(studentId, new HashMap<>());
        if (scores.isEmpty()) { System.out.println("  No quiz attempts found."); return; }
        System.out.println("\n  ── Your Quiz Scores ────────────────────");
        for (Map.Entry<String, Double> e : scores.entrySet()) {
            String title;
            try { title = findById(e.getKey()).getTitle(); } catch (Exception ex) { title = e.getKey(); }
            System.out.printf("  %-30s : %.1f%%%n", title, e.getValue());
        }
        System.out.println("  ────────────────────────────────────────");
    }

    public void deleteQuiz(String instructorId, String quizId) {
        Quiz quiz = findById(quizId);
        Course course = courseService.findById(quiz.getCourseId());
        if (!course.getInstructorId().equals(instructorId)) throw new SecurityException("You do not own this quiz.");
        quizzesById.remove(quizId);
        course.removeQuiz(quizId);
        System.out.println("  ✔ Quiz deleted: " + quizId);
    }

    public void loadDemoData(String drSarahId, String courseId1, String courseId2) {
        Quiz q1 = createQuiz(drSarahId, courseId1, "Java Basics Quiz", 30, 60.0);
        addQuestion(drSarahId, q1.getQuizId(), "Which keyword defines a class in Java?",
                new String[]{"define", "class", "struct", "object"}, 'B', 5);
        addQuestion(drSarahId, q1.getQuizId(), "Default value of int in Java?",
                new String[]{"null", "1", "0", "-1"}, 'C', 5);
        addQuestion(drSarahId, q1.getQuizId(), "Which is NOT an OOP pillar?",
                new String[]{"Inheritance", "Polymorphism", "Compilation", "Encapsulation"}, 'C', 5);
        addQuestion(drSarahId, q1.getQuizId(), "Entry point method of a Java program?",
                new String[]{"start()", "run()", "main()", "init()"}, 'C', 5);

        Quiz q2 = createQuiz(drSarahId, courseId2, "DSA Fundamentals Quiz", 20, 70.0);
        addQuestion(drSarahId, q2.getQuizId(), "Time complexity of binary search?",
                new String[]{"O(n)", "O(log n)", "O(n²)", "O(1)"}, 'B', 10);
        addQuestion(drSarahId, q2.getQuizId(), "Which data structure uses LIFO?",
                new String[]{"Queue", "Stack", "LinkedList", "Tree"}, 'B', 10);
    }

}
