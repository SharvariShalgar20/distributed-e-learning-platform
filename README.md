# Distributed E-Learning Platform (Console Version)

## 📌 Project Overview

This project is a backend-focused **Distributed E-Learning Platform** inspired by modern platforms like Udemy.
Currently built using **Core Java** with a console-based interface, following proper OOP principles and clean layered architecture.

The goal of this project is to **progressively evolve** from a basic console application into a fully distributed microservices-based system — phase by phase.

---

## 🧱 Current Implementation (Phase 1 – Core Java)

### ✔ Tech Stack

| Tool | Purpose |
|------|---------|
| Java (OOP) | Core language |
| IntelliJ IDEA | IDE |
| Git & GitHub | Version control |
| Console UI | User interaction |
| Java Collections | In-memory storage (`HashMap`, `ArrayList`) |

---

## 🏗 Architecture Structure

The project follows a **layered architecture** with clear separation of concerns:

```
com.sharvari.elearningplatform
│
├── model
│   ├── User.java              ← Abstract base class (Abstraction + Inheritance)
│   ├── Student.java           ← Extends User; tracks enrollments & progress
│   ├── Instructor.java        ← Extends User; manages courses
│   ├── Course.java            ← Course entity with publish/draft lifecycle
│   ├── Enrollment.java        ← Links Student ↔ Course with progress tracking
│   ├── Quiz.java              ← Quiz with configurable pass mark & time limit
│   └── Question.java          ← MCQ question (4 options, auto-grading)
│
├── service
│   ├── UserService.java       ← Interface: user registration, auth, CRUD contract
│   ├── CourseService.java     ← Interface: course creation, search, publish contract
│   ├── EnrollmentService.java ← Interface: enroll/drop, progress tracking contract
│   └── QuizService.java       ← Interface: quiz creation, question mgmt contract
│
├── service.impl
│   ├── UserServiceImpl.java       ← In-memory implementation of UserService
│   ├── CourseServiceImpl.java     ← In-memory implementation of CourseService
│   ├── EnrollmentServiceImpl.java ← In-memory implementation of EnrollmentService
│   └── QuizServiceImpl.java       ← In-memory implementation of QuizService
│
├── repository
│   ├── UserRepository.java        ← Interface: reserved for JDBC layer
│   ├── CourseRepository.java      ← Interface: reserved for JDBC layer
│   ├── EnrollmentRepository.java  ← Interface: reserved for JDBC layer
│   └── QuizRepository.java        ← Interface: reserved for JDBC layer
│
├── exception
│   ├── UserNotFoundException.java
│   ├── CourseNotFoundException.java
│   └── AuthenticationException.java
│
├── util
│   ├── IdGenerator.java       ← Thread-safe auto-incrementing ID generator
│   └── InputValidator.java    ← Email, password, name validation helpers
│
└── main
    └── Main.java              ← Application entry point + console menu UI
```

---

### 📌 Layer Explanation

| Layer | Package | Responsibility |
|-------|---------|---------------|
| **Model** | `model` | Domain entities — core business objects |
| **Service Interface** | `service` | Business contracts — defines what can be done |
| **Service Implementation** | `service.impl` | Concrete logic — how things are done (in-memory now, swappable later) |
| **Repository** | `repository` | Data access contracts — reserved for future JDBC/database phase |
| **Exception** | `exception` | Custom application exceptions for clean error handling |
| **Util** | `util` | Reusable helpers — ID generation, input validation |
| **Main** | `main` | Application entry point — console menus and user interaction |

---

## 🎯 OOP Concepts Applied

| Concept | Where Applied |
|---------|--------------|
| **Abstraction** | `User` is abstract; `Service` interfaces define contracts without implementation |
| **Inheritance** | `Student` and `Instructor` both extend `User` |
| **Polymorphism** | `displayDashboard()` renders differently for Student vs Instructor |
| **Encapsulation** | All model fields are `private`, accessed only through getters/setters |
| **Interfaces** | `Service` and `Repository` layers are fully interface-driven |
| **Exception Handling** | Custom exceptions + `try-catch` throughout service and UI layers |
| **Collections** | `HashMap`, `ArrayList`, `Optional` used across all service implementations |

---

## ✅ Features Implemented

### 👩‍🎓 Student
- Register & Login
- Browse all published courses
- Search courses by title or category
- Enroll in / Drop courses
- Track progress per course (visual progress bar `[████░░░░░░]`)
- Take MCQ quizzes with real-time auto-grading
- View quiz scores and overall progress
- Change password

### 👨‍🏫 Instructor
- Register & Login
- Create, Edit, Delete courses
- Publish / Unpublish courses (draft → live lifecycle)
- Create quizzes for any owned course
- Add MCQ questions with marks and correct answer
- View student enrollments per course
- Delete quizzes
- Change password

---

## ▶ How to Run

### Using Terminal
```bash
# From the project root directory
mkdir -p out
find src -name "*.java" | xargs javac -d out

# Run the application
java -cp out com.sharvari.elearningplatform.main.Main
```

### Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Mark `src` as the **Sources Root** (right-click → Mark Directory As → Sources Root)
3. Run `Main.java`

---

## 🔐 Demo Login Credentials (Pre-loaded)

| Role | Email | Password |
|------|-------|----------|
| Student | alice@email.com | alice123 |
| Student | bob@email.com | bob123 |
| Instructor | sarah@email.com | sarah123 |
| Instructor | mark@email.com | mark123 |

---

## 🧪 Sample Walkthrough

```
1. Start the app → Main Menu appears
2. Login as Alice (student) → alice@email.com / alice123
3. Select "Browse & Enroll" → view published courses
4. Enroll in "Java Programming Fundamentals"
5. Select "Update Course Progress" → enter 50%
6. Select "Take a Quiz" → attempt "Java Basics Quiz"
7. Answer MCQ questions → view your result and score
8. Select "My Enrolled Courses" → see progress bar
9. Logout → login as sarah@email.com (instructor)
10. View enrollments → see Alice's progress
```

---

## 🗺 Evolution Roadmap

### ✅ Phase 1 — Core Java (Current)
- Console-based UI
- In-memory storage via Java Collections
- Full OOP model with service/impl/repository layering
- Interface-driven design ready for swapping implementations

### 🔜 Phase 2 — JDBC + Database
- Implement `UserRepository`, `CourseRepository` etc. with JDBC
- Connect to MySQL / PostgreSQL
- Replace `HashMap` stores in `*ServiceImpl` with repository calls
- Add connection pooling (HikariCP)

### 🔜 Phase 3 — REST API (Spring Boot)
- Expose all features as REST endpoints
- Replace console UI with HTTP layer
- Add JWT-based authentication
- Integrate Swagger/OpenAPI documentation

### 🔜 Phase 4 — Microservices
- Split into independent services: `user-service`, `course-service`, `enrollment-service`, `quiz-service`
- Introduce API Gateway
- Add service discovery (Eureka)
- Async communication via Kafka or RabbitMQ

### 🔜 Phase 5 — Cloud & Deployment
- Dockerize each microservice
- Kubernetes orchestration
- CI/CD pipeline (GitHub Actions)
- Cloud deployment (AZURE / GCP)

---

## 👩‍💻 Author

**Sharvari**
Distributed E-Learning Platform — Phase 1
Built with Core Java | OOP | Clean Architecture