package com.Sharvari.elearningPlatform.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger userCounter       = new AtomicInteger(1000);
    private static final AtomicInteger courseCounter     = new AtomicInteger(2000);
    private static final AtomicInteger enrollmentCounter = new AtomicInteger(3000);
    private static final AtomicInteger quizCounter       = new AtomicInteger(4000);
    private static final AtomicInteger questionCounter   = new AtomicInteger(5000);

    public static String generateUserId(){
        return "USR-" + userCounter.getAndIncrement();
    }

    public static String generateCourseId(){
        return "CRS-" + courseCounter.getAndIncrement();
    }

    public static String generateEnrollmentId() {
        return "ENR-" + enrollmentCounter.getAndIncrement();
    }

    public static String generateQuizId(){
        return "QUZ-" + quizCounter.getAndIncrement();
    }

    public static String generateQuestionId(){
        return "QST-" + questionCounter.getAndIncrement();
    }

}
