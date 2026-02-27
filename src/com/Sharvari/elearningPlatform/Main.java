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
            case "0": return false;
            default:  System.out.println("  ⚠ Invalid option.");
        }
        return true;
    }


    private static void showStudentMenu(Student student) {
        student.displayDashboard();
        System.out.println("  ┌─── STUDENT MENU ─────────────────┐");
        System.out.println("  │  1. Browse & Enroll in Courses   │");
        System.out.println("  │  2. My Enrolled Courses          │");
        System.out.println("  │  3. Update Course Progress       │");
        System.out.println("  │  4. Drop a Course                │");
        System.out.println("  │  5. Take a Quiz                  │");
        System.out.println("  │  6. View My Quiz Scores          │");
        System.out.println("  │  7. Search Courses               │");
        System.out.println("  │  8. Change Password              │");
        System.out.println("  │  0. Logout                       │");
        System.out.println("  └──────────────────────────────────┘");
        System.out.print("  Choose: ");
        try {
            switch (scanner.nextLine().trim()) {
                case "1": doBrowseAndEnroll(student);     break;
                case "2": doViewEnrolledCourses(student); break;
                case "3": doUpdateProgress(student);      break;
                case "4": doDropCourse(student);          break;
                case "5": doTakeQuiz(student);            break;
                case "6": quizService.displayStudentScores(student.getUserId()); break;
                case "7": doSearchCourses();              break;
                case "8": doChangePassword(student);      break;
                case "0": logout();                       break;
                default:  System.out.println("  ⚠ Invalid option.");
            }
        } catch (Exception e) { System.out.println("  ✘ Error: " + e.getMessage()); }
    }

    private static void showInstructorMenu(Instructor instructor) {
        instructor.displayDashboard();
        System.out.println("  ┌─── INSTRUCTOR MENU ──────────────┐");
        System.out.println("  │  1. Create New Course            │");
        System.out.println("  │  2. My Courses                   │");
        System.out.println("  │  3. Publish / Unpublish Course   │");
        System.out.println("  │  4. Edit Course                  │");
        System.out.println("  │  5. Delete Course                │");
        System.out.println("  │  6. Create Quiz for Course       │");
        System.out.println("  │  7. Add Question to Quiz         │");
        System.out.println("  │  8. View Course Enrollments      │");
        System.out.println("  │  9. Delete Quiz                  │");
        System.out.println("  │  A. Change Password              │");
        System.out.println("  │  0. Logout                       │");
        System.out.println("  └──────────────────────────────────┘");
        System.out.print("  Choose: ");
        try {
            switch (scanner.nextLine().trim().toUpperCase()) {
                case "1": doCreateCourse(instructor);    break;
                //case "2": doViewMyCourses(instructor);   break;
                //case "3": doPublishUnpublish(instructor);break;
                //case "4": doEditCourse(instructor);      break;
                //case "5": doDeleteCourse(instructor);    break;
                //case "6": doCreateQuiz(instructor);      break;
                //case "7": doAddQuestion(instructor);     break;
                //case "8": doViewEnrollments(instructor); break;
                //case "9": doDeleteQuiz(instructor);      break;
                //case "A": doChangePassword(instructor);  break;
                case "0": logout();                      break;
                default:  System.out.println("  ⚠ Invalid option.");
            }
        } catch (Exception e) { System.out.println("  ✘ Error: " + e.getMessage()); }
    }

    // ── Main Menu Actions ────────────────────────────

    private static void doLogin() {
        System.out.println("\n  ── Login ─────────────────────────────");
        System.out.print("  Email   : "); String email    = scanner.nextLine().trim();
        System.out.print("  Password: "); String password = scanner.nextLine().trim();
        try {
            currentUser = userService.login(email, password);
        }
        catch (Exception e) {
            System.out.println("  ✘ " + e.getMessage());
        }
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
        printCourseList("  ── Published Courses ─────────────────", courseService.getPublishedCourses());
    }

    private static void printCourseList(String header, List<Course> courses) {
        System.out.println("\n" + header);

        if(courses.isEmpty()) {
            System.out.println(" No courses found. ");
        } else {
            courses.forEach( c -> System.out.println("  "+c));
            System.out.println("  ──────────────────────────────────────");
        }
    }

    // ── Student Actions ────────────────────────────

    private static void doBrowseAndEnroll(Student student) {
        printCourseList("  ── Available Courses ─────────────────", courseService.getPublishedCourses());
        System.out.print("  Enter Course ID to enroll (0 to cancel): ");
        String cid = scanner.nextLine().trim();
        if (!cid.equals("0")) enrollmentService.enrollStudent(student.getUserId(), cid);
    }

    private static void doViewEnrolledCourses(Student student) {
        System.out.println("\n  ── My Enrolled Courses ───────────────");
        enrollmentService.displayStudentProgress(student.getUserId());
    }


    private static void doUpdateProgress(Student student) {
        System.out.println("\n  ── Update Progress ───────────────────");
        enrollmentService.displayStudentProgress(student.getUserId());
        System.out.print("  Enter Course ID: ");
        String cid = scanner.nextLine().trim();
        System.out.print("  New progress (0-100): ");
        try {
            double p = Double.parseDouble(scanner.nextLine().trim());
            enrollmentService.updateProgress(student.getUserId(), cid, p);
        } catch (NumberFormatException e) { System.out.println("  ✘ Invalid number."); }
    }

    private static void doDropCourse(Student student) {
        System.out.println("\n  ── Drop a Course ─────────────────────");
        enrollmentService.displayStudentProgress(student.getUserId());
        System.out.print("  Enter Course ID to drop (0 to cancel): ");
        String cid = scanner.nextLine().trim();
        if (!cid.equals("0")) {
            System.out.print("  Are you sure? (yes/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("yes"))
                enrollmentService.dropCourse(student.getUserId(), cid);
        }
    }

    private static void doTakeQuiz(Student student) {
        System.out.println("\n  ── Take a Quiz ───────────────────────");
        List<String> enrolled = student.getEnrolledCourseIds();

        if (enrolled.isEmpty()) {
            System.out.println("  Not enrolled in any courses.");
            return;
        }
        System.out.println("  Your courses and quizzes:");
        for (String cid : enrolled) {
            try {
                Course c = courseService.findById(cid);
                System.out.println("  [" + cid + "] " + c.getTitle());
                for (Quiz q : quizService.getQuizzesByCourse(cid))
                    System.out.println("       → " + q.getQuizId() + " | " + q.getTitle());
            } catch (Exception ignored) {}
        }
        System.out.print("  Enter Quiz ID (0 to cancel): ");
        String qid = scanner.nextLine().trim();
        if (!qid.equals("0")) quizService.attemptQuiz(student.getUserId(), qid, scanner);
    }

    private static void doSearchCourses() {
        System.out.println("\n  Search by: 1) Title  2) Category");
        System.out.print("  Choice: ");
        String opt = scanner.nextLine().trim();
        if (opt.equals("1")) {
            System.out.print("  Keyword : ");
            String kw = scanner.nextLine().trim();
            printCourseList("  ── Results ───────────────────────────", courseService.searchCoursesByTitle(kw));
        } else if (opt.equals("2")) {
            System.out.print("  Category: "); String cat = scanner.nextLine().trim();
            printCourseList("  ── Results ───────────────────────────", courseService.searchCoursesByCategory(cat));
        }
    }

    private static void doChangePassword(User user) {
        System.out.println("\n  ── Change Password ───────────────────");
        System.out.print("  Old Password: "); String oldP = scanner.nextLine().trim();
        System.out.print("  New Password: "); String newP = scanner.nextLine().trim();
        userService.changePassword(user.getUserId(), oldP, newP);
    }


    private static void logout() {
        System.out.println("  ✔ Logged out. Goodbye, " + currentUser.getName() + "!");
        currentUser = null;
    }

    // ── Instructor Actions ────────────────────────────
    private static void doCreateCourse(Instructor instructor) {
        System.out.println("\n  ── Create New Course ─────────────────");
        System.out.print("  Title       : "); String title = scanner.nextLine().trim();
        System.out.print("  Description : "); String desc  = scanner.nextLine().trim();
        System.out.print("  Category    : "); String cat   = scanner.nextLine().trim();
        System.out.print("  Duration (h): ");
        try {
            int hours = Integer.parseInt(scanner.nextLine().trim());
            courseService.createCourse(instructor.getUserId(), title, desc, cat, hours);
        } catch (NumberFormatException e) { System.out.println("  ✘ Invalid duration."); }
    }





}