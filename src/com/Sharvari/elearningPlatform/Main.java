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
        printBanner();

        boolean running = true;
//        while (running) {
//            if (currentUser == null) {
//                running = showMainMenu();
//            }
//            else if (currentUser instanceof Student){
//                showStudentMenu((Student) currentUser);
//            }
//            else if (currentUser instanceof Instructor) {
//                showInstructorMenu((Instructor) currentUser);
//            }
//        }
        System.out.println("\n  Thank you for using E-Learning Platform. Goodbye!\n");
        scanner.close();
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

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════╗");
        System.out.println("  ║        E-LEARNING MANAGEMENT SYSTEM        ║");
        System.out.println("  ╚════════════════════════════════════════════╝");
    }


    private static boolean showMainMenu() {
        System.out.println("\n  ┌─── MAIN MENU ────────────────────┐");
        System.out.println("  │  1. Login                        │");
        System.out.println("  │  2. Register as Student          │");
        System.out.println("  │  3. Register as Instructor       │");
        System.out.println("  │  4. Browse Published Courses     │");
        System.out.println("  │  0. Exit                         │");
        System.out.println("  └──────────────────────────────────┘");
        System.out.print("  Choose: ");
        switch (scanner.nextLine().trim()) {
            case "1": doLogin();             break;
            case "2": doRegisterStudent();   break;
            case "3": doRegisterInstructor();break;
            case "4": doBrowseCourses();     break;
            //case "0": return false;
            default:  System.out.println("  ⚠ Invalid option.");
        }
        return true;
    }

    private static void doLogin() {
        System.out.println("\n  ── Login ─────────────────────────────");
        System.out.print("  Email   : "); String email    = scanner.nextLine().trim();
        System.out.print("  Password: "); String password = scanner.nextLine().trim();
        try { currentUser = userService.login(email, password); }
        catch (Exception e) { System.out.println("  ✘ " + e.getMessage()); }
    }

    private static void doRegisterStudent() {
        System.out.println("\n  ── Register Student ──────────────────");
        System.out.print("  Name    : "); String name     = scanner.nextLine().trim();
        System.out.print("  Email   : "); String email    = scanner.nextLine().trim();
        System.out.print("  Password: "); String password = scanner.nextLine().trim();
        try { userService.registerStudent(name, email, password); }
        catch (Exception e) { System.out.println("  ✘ " + e.getMessage()); }
    }

    private static void doRegisterInstructor() {
        System.out.println("\n  ── Register Instructor ───────────────");
        System.out.print("  Name     : "); String name      = scanner.nextLine().trim();
        System.out.print("  Email    : "); String email     = scanner.nextLine().trim();
        System.out.print("  Password : "); String password  = scanner.nextLine().trim();
        System.out.print("  Expertise: "); String expertise = scanner.nextLine().trim();
        try { userService.registerInstructor(name, email, password, expertise); }
        catch (Exception e) { System.out.println("  ✘ " + e.getMessage()); }
    }

    private static void doBrowseCourses() {
        printCourseList(courseService.getPublishedCourses());
    }

    private static void printCourseList(List<Course> courses) {
        System.out.println("\n" + "  ── Published Courses ─────────────────");

        if(courses.isEmpty()) {
            System.out.println(" No courses found. ");
        } else {
            courses.forEach( c -> System.out.println("  "+c));
            System.out.println("  ──────────────────────────────────────");
        }
    }

}