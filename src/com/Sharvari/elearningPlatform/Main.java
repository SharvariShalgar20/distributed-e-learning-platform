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
        loadDemoData();
    }

    private static void initServices() {
        userService       = new UserService();
        courseService     = new CourseService(userService);
        enrollmentService = new EnrollmentService(userService, courseService);
        quizService       = new QuizService(userService, courseService, enrollmentService);
    }

    private static void loadDemoData() {
        System.out.println("\n  Loading demo data...");
        userService.loadDemoData();
        List<Instructor> instructors = userService.getAllInstructors();
        String drSarahId  = instructors.get(0).getUserId();
        String profMarkId = instructors.get(1).getUserId();
        courseService.loadDemoData(drSarahId, profMarkId);
        List<Course> courses = courseService.getPublishedCourses();
        quizService.loadDemoData(drSarahId, courses.get(0).getCourseId(), courses.get(1).getCourseId());
        System.out.println("  ──────────────────────────────────────────");
        System.out.println("  Demo Logins:");
        System.out.println("   Student    → alice@email.com / alice123");
        System.out.println("   Student    → bob@email.com   / bob123");
        System.out.println("   Instructor → sarah@email.com / sarah123");
        System.out.println("   Instructor → mark@email.com  / mark123");
        System.out.println("  ──────────────────────────────────────────");
    }


}
