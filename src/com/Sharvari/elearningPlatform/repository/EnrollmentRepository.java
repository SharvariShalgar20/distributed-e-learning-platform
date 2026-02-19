package com.Sharvari.elearningPlatform.repository;

import com.Sharvari.elearningPlatform.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    void save(Enrollment enrollment);
    Optional<Enrollment> findById(String enrollmentId);
    Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId);
    List<Enrollment> findByStudentId(String studentId);
    List<Enrollment> findByCourseId(String courseId);
    void update(Enrollment enrollment);
    void delete(String enrollmentId);
}
