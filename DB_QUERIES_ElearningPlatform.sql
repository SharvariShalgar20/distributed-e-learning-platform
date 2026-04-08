CREATE DATABASE IF NOT EXISTS elearning_platform
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;


USE elearning_platform;

-- ── 1. USERS ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id          VARCHAR(20)     NOT NULL,
    name             VARCHAR(100)    NOT NULL,
    email            VARCHAR(150)    NOT NULL,
    password         VARCHAR(255)    NOT NULL,      -- store hashed in production
    role             ENUM('STUDENT','INSTRUCTOR') NOT NULL,
    expertise        VARCHAR(200)    DEFAULT NULL,  -- INSTRUCTOR only
    overall_progress DECIMAL(5,2)    DEFAULT 0.00,  -- STUDENT only
    created_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users   PRIMARY KEY (user_id),
    CONSTRAINT uq_email   UNIQUE (email)
    );


-- ── 2. COURSES ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    course_id       VARCHAR(20)     NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT            NOT NULL,
    instructor_id   VARCHAR(20)     NOT NULL,
    category        VARCHAR(100)    NOT NULL,
    duration_hours  INT             NOT NULL CHECK (duration_hours > 0),
    is_published    TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_courses          PRIMARY KEY (course_id),
    CONSTRAINT fk_course_instructor
    FOREIGN KEY (instructor_id)
    REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

-- ── 3. ENROLLMENTS ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id   VARCHAR(20)     NOT NULL,
    student_id      VARCHAR(20)     NOT NULL,
    course_id       VARCHAR(20)     NOT NULL,
    enrollment_date DATE            NOT NULL,
    progress        DECIMAL(5,2)    NOT NULL DEFAULT 0.00
    CHECK (progress >= 0 AND progress <= 100),
    status          ENUM('ACTIVE','COMPLETED','DROPPED') NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT pk_enrollments      PRIMARY KEY (enrollment_id),
    CONSTRAINT fk_enroll_student
    FOREIGN KEY (student_id)
    REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_enroll_course
    FOREIGN KEY (course_id)
    REFERENCES courses (course_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    -- A student can be enrolled in a course only once (non-dropped)
    CONSTRAINT uq_active_enrollment
    UNIQUE (student_id, course_id)
    );

-- ── 4. QUIZZES ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS quizzes (
    quiz_id             VARCHAR(20)     NOT NULL,
    title               VARCHAR(200)    NOT NULL,
    course_id           VARCHAR(20)     NOT NULL,
    time_limit_minutes  INT             NOT NULL DEFAULT 30,
    passing_score       DECIMAL(5,2)    NOT NULL DEFAULT 50.00
    CHECK (passing_score >= 0 AND passing_score <= 100),
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_quizzes  PRIMARY KEY (quiz_id),
    CONSTRAINT fk_quiz_course
    FOREIGN KEY (course_id)
    REFERENCES courses (course_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );


-- ── 5. QUESTIONS ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS questions (
    question_id     VARCHAR(20)     NOT NULL,
    quiz_id         VARCHAR(20)     NOT NULL,
    question_text   TEXT            NOT NULL,
    option_a        VARCHAR(500)    NOT NULL,
    option_b        VARCHAR(500)    NOT NULL,
    option_c        VARCHAR(500)    NOT NULL,
    option_d        VARCHAR(500)    NOT NULL,
    correct_answer  CHAR(1)         NOT NULL CHECK (correct_answer IN ('A','B','C','D')),
    marks           INT             NOT NULL CHECK (marks > 0),

    CONSTRAINT pk_questions     PRIMARY KEY (question_id),
    CONSTRAINT fk_question_quiz
    FOREIGN KEY (quiz_id)
    REFERENCES quizzes (quiz_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );


-- ── 6. QUIZ SCORES ───────────────────────────────────────────

CREATE TABLE IF NOT EXISTS quiz_scores (
    student_id      VARCHAR(20)     NOT NULL,
    quiz_id         VARCHAR(20)     NOT NULL,
    score_percent   DECIMAL(5,2)    NOT NULL
    CHECK (score_percent >= 0 AND score_percent <= 100),
    attempted_at    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_quiz_scores   PRIMARY KEY (student_id, quiz_id),
    CONSTRAINT fk_score_student
    FOREIGN KEY (student_id)
    REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_score_quiz
    FOREIGN KEY (quiz_id)
    REFERENCES quizzes (quiz_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

--  INDEXES

CREATE INDEX idx_courses_instructor  ON courses     (instructor_id);
CREATE INDEX idx_courses_published   ON courses     (is_published);
CREATE INDEX idx_courses_category    ON courses     (category);
CREATE INDEX idx_enrollments_student ON enrollments (student_id);
CREATE INDEX idx_enrollments_course  ON enrollments (course_id);
CREATE INDEX idx_quizzes_course      ON quizzes     (course_id);
CREATE INDEX idx_questions_quiz      ON questions   (quiz_id);
CREATE INDEX idx_scores_student      ON quiz_scores (student_id);
