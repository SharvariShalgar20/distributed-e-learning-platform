package com.Sharvari.elearningPlatform;

import com.Sharvari.elearningPlatform.model.*;
import com.Sharvari.elearningPlatform.service.*;

import java.util.List;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static UserService       userService;
    private static CourseService     courseService;
    private static EnrollmentService enrollmentService;
    private static QuizService       quizService;
    private static User currentUser = null;

    public static void main(String[] args) {

        initServices();
    }

    private static void initServices() {
        userService       = new UserService();
        courseService     = new CourseService(userService);
        enrollmentService = new EnrollmentService(userService, courseService);
        quizService       = new QuizService(userService, courseService, enrollmentService);
    }
}
