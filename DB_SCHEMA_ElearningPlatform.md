# Database Schema — E-Learning Platform

**Database:** `elearning_platform`
**Engine:** MySQL 8.x · InnoDB
**Character Set:** `utf8mb4` · Collation: `utf8mb4_unicode_ci`

---

## Table of Contents

1. [Entity Relationship Overview](#entity-relationship-overview)
2. [Tables](#tables)
    - [users](#1-users)
    - [courses](#2-courses)
    - [enrollments](#3-enrollments)
    - [quizzes](#4-quizzes)
    - [questions](#5-questions)
    - [quiz_scores](#6-quiz_scores)
3. [Foreign Key Relationships](#foreign-key-relationships)
4. [Indexes](#indexes)
5. [Views](#views)
    - [v_active_enrollments](#v_active_enrollments)
    - [v_quiz_scoreboard](#v_quiz_scoreboard)
6. [ID Format Reference](#id-format-reference)
7. [Cascade Behavior](#cascade-behavior)
8. [Seed Data Summary](#seed-data-summary)
9. [Design Notes & Constraints](#design-notes--constraints)

---

## Entity Relationship Overview

```
users (INSTRUCTOR)
    │
    │ 1 : N
    ▼
courses ──────────────────────────── quizzes
    │                                    │
    │ 1 : N                              │ 1 : N
    ▼                                    ▼
enrollments                          questions
(users/STUDENT ──────────────────►)
                                     quiz_scores
                                  (users/STUDENT ◄──────────►  quizzes)
```

A single `users` table stores both **Students** and **Instructors**, differentiated by the `role` column. Instructors own courses; students enroll in courses and attempt quizzes.

---

## Tables

---

### 1. `users`

Stores all platform users. Both Students and Instructors share this table (single-table inheritance pattern), distinguished by the `role` column.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `user_id` | `VARCHAR(20)` | NOT NULL | — | Primary key. Format: `USR-{number}` |
| `name` | `VARCHAR(100)` | NOT NULL | — | Full display name (min 2 chars) |
| `email` | `VARCHAR(150)` | NOT NULL | — | Unique login email address |
| `password` | `VARCHAR(255)` | NOT NULL | — | Plain text in dev; hash in production |
| `role` | `ENUM('STUDENT','INSTRUCTOR')` | NOT NULL | — | Determines user type |
| `expertise` | `VARCHAR(200)` | NULL | `NULL` | Instructor only — e.g. "Java & Spring Boot" |
| `overall_progress` | `DECIMAL(5,2)` | NULL | `0.00` | Student only — average progress across all active enrollments (%) |
| `created_at` | `TIMESTAMP` | NOT NULL | `CURRENT_TIMESTAMP` | Account creation time |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_users` | PRIMARY KEY | `user_id` |
| `uq_email` | UNIQUE | `email` |

**Notes:**
- `expertise` is only meaningful when `role = 'INSTRUCTOR'`; it is `NULL` for students.
- `overall_progress` is only meaningful when `role = 'STUDENT'`; it is recalculated by `EnrollmentService` every time a student's course progress is updated.
- Passwords must be hashed (e.g. BCrypt) before storing in production.

---

### 2. `courses`

Represents a course created by an instructor. A course must be published before students can enroll.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `course_id` | `VARCHAR(20)` | NOT NULL | — | Primary key. Format: `CRS-{number}` |
| `title` | `VARCHAR(200)` | NOT NULL | — | Course display title |
| `description` | `TEXT` | NOT NULL | — | Full course description |
| `instructor_id` | `VARCHAR(20)` | NOT NULL | — | FK → `users.user_id` (must be INSTRUCTOR) |
| `category` | `VARCHAR(100)` | NOT NULL | — | e.g. "Programming", "Data Science" |
| `duration_hours` | `INT` | NOT NULL | — | Estimated duration in hours (must be > 0) |
| `is_published` | `TINYINT(1)` | NOT NULL | `0` | `0` = Draft, `1` = Published |
| `created_at` | `TIMESTAMP` | NOT NULL | `CURRENT_TIMESTAMP` | Course creation time |
| `updated_at` | `TIMESTAMP` | NOT NULL | `CURRENT_TIMESTAMP` | Auto-updated on any row change |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_courses` | PRIMARY KEY | `course_id` |
| `fk_course_instructor` | FOREIGN KEY | `instructor_id` → `users.user_id` |
| — | CHECK | `duration_hours > 0` |

**Notes:**
- Students can only enroll in courses where `is_published = 1`.
- Deleting a user who is an instructor cascades and deletes all their courses (and everything beneath them).

---

### 3. `enrollments`

Tracks which student is enrolled in which course, along with their current progress and status.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `enrollment_id` | `VARCHAR(20)` | NOT NULL | — | Primary key. Format: `ENR-{number}` |
| `student_id` | `VARCHAR(20)` | NOT NULL | — | FK → `users.user_id` (must be STUDENT) |
| `course_id` | `VARCHAR(20)` | NOT NULL | — | FK → `courses.course_id` |
| `enrollment_date` | `DATE` | NOT NULL | — | Date the student enrolled |
| `progress` | `DECIMAL(5,2)` | NOT NULL | `0.00` | Completion percentage (0.00 – 100.00) |
| `status` | `ENUM('ACTIVE','COMPLETED','DROPPED')` | NOT NULL | `'ACTIVE'` | Current enrollment state |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_enrollments` | PRIMARY KEY | `enrollment_id` |
| `fk_enroll_student` | FOREIGN KEY | `student_id` → `users.user_id` |
| `fk_enroll_course` | FOREIGN KEY | `course_id` → `courses.course_id` |
| `uq_active_enrollment` | UNIQUE | `(student_id, course_id)` |
| — | CHECK | `progress >= 0 AND progress <= 100` |

**Status transitions:**

```
ACTIVE ──► COMPLETED   (when progress reaches 100.0)
ACTIVE ──► DROPPED     (when student drops the course)
```

**Notes:**
- The `UNIQUE (student_id, course_id)` constraint prevents a student from having duplicate enrollment records for the same course. When a student drops and re-enrolls, the old record should be deleted first.
- `progress = 100.00` automatically sets `status = 'COMPLETED'` in the Java `Enrollment` model.

---

### 4. `quizzes`

A quiz belongs to exactly one course and contains one or more questions.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `quiz_id` | `VARCHAR(20)` | NOT NULL | — | Primary key. Format: `QUZ-{number}` |
| `title` | `VARCHAR(200)` | NOT NULL | — | Quiz display title |
| `course_id` | `VARCHAR(20)` | NOT NULL | — | FK → `courses.course_id` |
| `time_limit_minutes` | `INT` | NOT NULL | `30` | Time allowed to complete the quiz (minutes) |
| `passing_score` | `DECIMAL(5,2)` | NOT NULL | `50.00` | Minimum percentage needed to pass (0–100) |
| `created_at` | `TIMESTAMP` | NOT NULL | `CURRENT_TIMESTAMP` | Quiz creation time |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_quizzes` | PRIMARY KEY | `quiz_id` |
| `fk_quiz_course` | FOREIGN KEY | `course_id` → `courses.course_id` |
| — | CHECK | `passing_score >= 0 AND passing_score <= 100` |

**Notes:**
- Only the instructor who owns the parent course can create or delete a quiz.
- A student must be actively enrolled in the course to attempt the quiz.

---

### 5. `questions`

Each row is one multiple-choice question belonging to a quiz. Exactly four options are stored as fixed columns (A, B, C, D).

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `question_id` | `VARCHAR(20)` | NOT NULL | — | Primary key. Format: `QST-{number}` |
| `quiz_id` | `VARCHAR(20)` | NOT NULL | — | FK → `quizzes.quiz_id` |
| `question_text` | `TEXT` | NOT NULL | — | The full question text |
| `option_a` | `VARCHAR(500)` | NOT NULL | — | Answer option A |
| `option_b` | `VARCHAR(500)` | NOT NULL | — | Answer option B |
| `option_c` | `VARCHAR(500)` | NOT NULL | — | Answer option C |
| `option_d` | `VARCHAR(500)` | NOT NULL | — | Answer option D |
| `correct_answer` | `CHAR(1)` | NOT NULL | — | One of: `'A'`, `'B'`, `'C'`, `'D'` |
| `marks` | `INT` | NOT NULL | — | Points awarded for a correct answer (must be > 0) |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_questions` | PRIMARY KEY | `question_id` |
| `fk_question_quiz` | FOREIGN KEY | `quiz_id` → `quizzes.quiz_id` |
| — | CHECK | `correct_answer IN ('A','B','C','D')` |
| — | CHECK | `marks > 0` |

**Notes:**
- Questions are loaded eagerly when their parent quiz is fetched (`QuizRepositoryImpl.loadQuestionsInto`).
- Total quiz marks = `SUM(marks)` across all questions for that quiz.

---

### 6. `quiz_scores`

Records the latest score achieved by a student on a specific quiz. Uses a composite primary key so that re-taking a quiz updates the existing row (upsert) rather than inserting a duplicate.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `student_id` | `VARCHAR(20)` | NOT NULL | — | FK → `users.user_id` (STUDENT) |
| `quiz_id` | `VARCHAR(20)` | NOT NULL | — | FK → `quizzes.quiz_id` |
| `score_percent` | `DECIMAL(5,2)` | NOT NULL | — | Score achieved (0.00 – 100.00) |
| `attempted_at` | `TIMESTAMP` | NOT NULL | `CURRENT_TIMESTAMP` | Time of last attempt; auto-updated on upsert |

**Constraints:**

| Name | Type | Column(s) |
|---|---|---|
| `pk_quiz_scores` | PRIMARY KEY | `(student_id, quiz_id)` |
| `fk_score_student` | FOREIGN KEY | `student_id` → `users.user_id` |
| `fk_score_quiz` | FOREIGN KEY | `quiz_id` → `quizzes.quiz_id` |
| — | CHECK | `score_percent >= 0 AND score_percent <= 100` |

**Notes:**
- Persisted via `INSERT … ON DUPLICATE KEY UPDATE` in `QuizRepositoryImpl.saveOrUpdateScore()`.
- Only the latest attempt is stored; historical attempts are not tracked in this schema.

---

## Foreign Key Relationships

```
users (user_id)
    ├──► courses.instructor_id          [1 instructor : N courses]
    ├──► enrollments.student_id         [1 student    : N enrollments]
    └──► quiz_scores.student_id         [1 student    : N quiz scores]

courses (course_id)
    ├──► enrollments.course_id          [1 course  : N enrollments]
    └──► quizzes.course_id              [1 course  : N quizzes]

quizzes (quiz_id)
    ├──► questions.quiz_id              [1 quiz    : N questions]
    └──► quiz_scores.quiz_id            [1 quiz    : N scores]
```

---

## Indexes

| Index Name | Table | Column(s) | Purpose |
|---|---|---|---|
| `pk_users` | `users` | `user_id` | Primary key lookup |
| `uq_email` | `users` | `email` | Unique login / duplicate check |
| `pk_courses` | `courses` | `course_id` | Primary key lookup |
| `idx_courses_instructor` | `courses` | `instructor_id` | `getCoursesByInstructor()` |
| `idx_courses_published` | `courses` | `is_published` | `getPublishedCourses()` |
| `idx_courses_category` | `courses` | `category` | `searchCoursesByCategory()` |
| `pk_enrollments` | `enrollments` | `enrollment_id` | Primary key lookup |
| `uq_active_enrollment` | `enrollments` | `(student_id, course_id)` | Prevent duplicate enrollments |
| `idx_enrollments_student` | `enrollments` | `student_id` | `getEnrollmentsByStudent()` |
| `idx_enrollments_course` | `enrollments` | `course_id` | `getEnrollmentsByCourse()` |
| `pk_quizzes` | `quizzes` | `quiz_id` | Primary key lookup |
| `idx_quizzes_course` | `quizzes` | `course_id` | `getQuizzesByCourse()` |
| `pk_questions` | `questions` | `question_id` | Primary key lookup |
| `idx_questions_quiz` | `questions` | `quiz_id` | Eager-load questions per quiz |
| `pk_quiz_scores` | `quiz_scores` | `(student_id, quiz_id)` | Upsert & lookup |
| `idx_scores_student` | `quiz_scores` | `student_id` | `displayStudentScores()` |

---

## Views

### `v_active_enrollments`

A convenience view that joins enrollments with student names and course titles. Excludes DROPPED enrollments.

```sql
SELECT
    e.enrollment_id,
    u.name          AS student_name,
    c.title         AS course_title,
    e.enrollment_date,
    e.progress,
    e.status
FROM enrollments e
JOIN users   u ON u.user_id   = e.student_id
JOIN courses c ON c.course_id = e.course_id
WHERE e.status != 'DROPPED';
```

**Sample usage:**
```sql
SELECT * FROM v_active_enrollments WHERE student_name = 'Charlie Brown';
```

---

### `v_quiz_scoreboard`

A convenience view that shows all quiz scores with student names, quiz titles, and course titles, sorted by score descending.

```sql
SELECT
    qs.student_id,
    u.name          AS student_name,
    q.title         AS quiz_title,
    c.title         AS course_title,
    qs.score_percent,
    qs.attempted_at
FROM quiz_scores qs
JOIN users   u ON u.user_id   = qs.student_id
JOIN quizzes q ON q.quiz_id   = qs.quiz_id
JOIN courses c ON c.course_id = q.course_id
ORDER BY qs.score_percent DESC;
```

**Sample usage:**
```sql
SELECT * FROM v_quiz_scoreboard WHERE course_title = 'Java Fundamentals';
```

---

## ID Format Reference

All IDs are generated by `IdGenerator.java` using `AtomicInteger` counters.

| Entity | Format | Starting Value | Example |
|---|---|---|---|
| User | `USR-{n}` | 1000 | `USR-1000` |
| Course | `CRS-{n}` | 2000 | `CRS-2001` |
| Enrollment | `ENR-{n}` | 3000 | `ENR-3000` |
| Quiz | `QUZ-{n}` | 4000 | `QUZ-4002` |
| Question | `QST-{n}` | 5000 | `QST-5001` |

---

## Cascade Behavior

All foreign keys are defined with `ON DELETE CASCADE ON UPDATE CASCADE`. The delete chains are:

```
DELETE user (instructor)
    └──► DELETE courses they own
              ├──► DELETE enrollments for those courses
              ├──► DELETE quizzes for those courses
              │         ├──► DELETE questions in those quizzes
              │         └──► DELETE quiz_scores for those quizzes
              └──► (enrollment cascade already covers students)

DELETE user (student)
    ├──► DELETE their enrollments
    └──► DELETE their quiz_scores

DELETE course
    ├──► DELETE its enrollments
    └──► DELETE its quizzes
              ├──► DELETE questions in those quizzes
              └──► DELETE quiz_scores for those quizzes

DELETE quiz
    ├──► DELETE its questions
    └──► DELETE its quiz_scores
```

---

## Seed Data Summary

The DDL script includes demo data for immediate testing:

**Users (4 rows)**

| user_id | name | role | email |
|---|---|---|---|
| USR-1000 | Alice Smith | INSTRUCTOR | alice@example.com |
| USR-1001 | Bob Jones | INSTRUCTOR | bob@example.com |
| USR-1002 | Charlie Brown | STUDENT | charlie@example.com |
| USR-1003 | Diana Prince | STUDENT | diana@example.com |

**Courses (4 rows)**

| course_id | title | instructor | published |
|---|---|---|---|
| CRS-2000 | Java Fundamentals | Alice Smith | ✅ Yes |
| CRS-2001 | Spring Boot Mastery | Alice Smith | ✅ Yes |
| CRS-2002 | Intro to Data Science | Bob Jones | ✅ Yes |
| CRS-2003 | Advanced ML | Bob Jones | ❌ Draft |

**Enrollments (3 rows)**

| enrollment_id | student | course | progress | status |
|---|---|---|---|---|
| ENR-3000 | Charlie Brown | Java Fundamentals | 40% | ACTIVE |
| ENR-3001 | Charlie Brown | Intro to Data Science | 10% | ACTIVE |
| ENR-3002 | Diana Prince | Spring Boot Mastery | 70% | ACTIVE |

**Quizzes (4 rows)**

| quiz_id | title | course | pass % | time |
|---|---|---|---|---|
| QUZ-4000 | Java Basics Quiz | CRS-2000 | 60% | 15 min |
| QUZ-4001 | OOP Concepts Quiz | CRS-2000 | 70% | 20 min |
| QUZ-4002 | Spring REST Quiz | CRS-2001 | 65% | 25 min |
| QUZ-4003 | Pandas & NumPy Quiz | CRS-2002 | 60% | 20 min |

**Questions (5 rows):** 3 for QUZ-4000, 2 for QUZ-4001.

**Quiz Scores (2 rows):** Charlie scored 83.33% on QUZ-4000; Diana scored 75% on QUZ-4002.

> Remove all `INSERT` statements from the DDL before deploying to production.

---

## Design Notes & Constraints

**Single-table inheritance for users** — Students and Instructors share the `users` table to keep queries simple and FK references clean. The tradeoff is that `expertise` is `NULL` for students and `overall_progress` is `NULL` for instructors; both are enforced in the Java service layer, not at the DB level.

**No history for quiz scores** — `quiz_scores` stores only the latest attempt per `(student_id, quiz_id)` pair. If you need attempt history, add a separate `quiz_attempts` table with a surrogate PK.

**`uq_active_enrollment` limitation** — The unique constraint on `(student_id, course_id)` prevents re-enrollment after a DROPPED record still exists in the table. The application must delete the DROPPED row before allowing re-enrollment, or the constraint should be changed to a partial index (not natively supported in MySQL; would require an application-level check).

**Passwords** — The `password` column is `VARCHAR(255)` to accommodate BCrypt hashes in production. Currently stored as plain text for development only.

**Timestamps** — All `created_at` / `updated_at` columns use `TIMESTAMP` (UTC). Ensure the MySQL server timezone is set to UTC to avoid daylight-saving inconsistencies.