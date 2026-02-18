package com.Sharvari.elearningPlatform;

import com.Sharvari.elearningPlatform.model.Course;
import com.Sharvari.elearningPlatform.service.CourseService;
import com.Sharvari.elearningPlatform.service.impl.CourseServiceImpl;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        CourseService courseService = new CourseServiceImpl();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== E-Learning Platform =====");
            System.out.println("1. Create Course");
            System.out.println("2. View Courses");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Course ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter Description: ");
                    String desc = scanner.nextLine();

                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();

                    Course course = new Course(id, title, desc, price, 1);
                    courseService.createCourse(course);
                    break;

                case 2:
                    courseService.getAllCourses()
                            .forEach(c -> System.out.println(c.getTitle()));
                    break;

                case 3:
                    System.exit(0);

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
