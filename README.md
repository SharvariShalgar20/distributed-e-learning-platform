# Distributed E-Learning Platform (Console Version)

## 📌 Project Overview

This project is a backend-focused Distributed E-Learning Platform inspired by modern platforms like Udemy.  
Currently, it is built using Core Java with a console-based interface following proper OOP principles and clean architecture.

The goal of this project is to progressively evolve it from a basic console application into a fully distributed microservices-based system.

---

## 🧱 Current Implementation (Phase 1 – Core Java)

### ✔ Tech Stack
- Java (OOP)
- IntelliJ IDEA
- Git & GitHub
- Console-based UI
- In-memory storage using Collections

---

## 🏗 Architecture Structure

The project follows layered architecture:

```
com.sharvari.elearningplatform
│
├── model
│   ├── User.java
│   ├── Student.java
│   ├── Instructor.java
│   └── Course.java
│
├── service
│   └── CourseService.java
│
├── service.impl
│   └── CourseServiceImpl.java
│
├── repository
│   └── (Reserved for JDBC / Database layer)
│
├── exception
│   └── (Custom exceptions will be added here)
│
├── util
│   └── (Utility classes like IdGenerator, Validators)
│
└── main
    └── Main.java
```


### 📌 Layer Explanation

- **model** → Domain entities (core business objects)
- **service** → Interfaces (business contracts)
- **service.impl** → Concrete implementations of services
- **repository** → Database interaction layer (future JDBC phase)
- **exception** → Custom application exceptions
- **util** → Reusable helper classes
- **main** → Application entry point (console UI)

