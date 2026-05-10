package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.exception.AuthenticationException;
import com.Sharvari.elearningPlatform.exception.UserNotFoundException;
import com.Sharvari.elearningPlatform.model.Instructor;
import com.Sharvari.elearningPlatform.model.Student;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.util.IdGenerator;
import com.Sharvari.elearningPlatform.util.InputValidator;
import com.Sharvari.elearningPlatform.repository.impl.UserRepositoryImpl;

import java.util.*;

public class UserService {

    private final Map<String, User> usersById    = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();


    private final UserRepositoryImpl userRepository;

    public UserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public Student registerStudent(String name, String email, String password) {
        validateRegistrationInput(name, email, password);
        String id = IdGenerator.generateUserId();
        Student student = new Student(id, name, email, password);

        userRepository.save(student);

        System.out.println("  ✔ Student registered! ID: " + id);
        return student;
    }

    public Instructor registerInstructor(String name, String email, String password, String expertise) {
        validateRegistrationInput(name, email, password);
        if (!InputValidator.isNotBlank(expertise)) throw new IllegalArgumentException("Expertise cannot be blank.");
        String id = IdGenerator.generateUserId();
        Instructor instructor = new Instructor(id, name, email, password, expertise);

        userRepository.save(instructor);

        System.out.println("  ✔ Instructor registered! ID: " + id);
        return instructor;
    }


    private void validateRegistrationInput(String name, String email, String password) {
        if (!InputValidator.isValidName(name))       throw new IllegalArgumentException("Name must be at least 2 characters.");

        if (!InputValidator.isValidEmail(email))     throw new IllegalArgumentException("Invalid email format.");

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered.");

        if (!InputValidator.isValidPassword(password)) throw new IllegalArgumentException("Password must be at least 6 characters.");
    }

    // ── Authentication ───────────────────────────────────────────────────────

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (!user.getPassword().equals(password))
            throw new AuthenticationException("Invalid email or password.");

        System.out.println("  ✔ Login successful! Welcome, " + user.getName() + ".");

        return user;
    }


    // ── Lookups ──────────────────────────────────────────────────────────────

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        for (User u : userRepository.findAll()) {
            if (u instanceof Student)
                list.add((Student) u);
        }

        return list;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();

        for (User u : userRepository.findAll())
            if (u instanceof Instructor)
                list.add((Instructor) u);

        return list;
    }

    // ── Mutations ────────────────────────────────────────────────────────────

    public void updateUserName(String userId, String newName) {
        if (!InputValidator.isValidName(newName))
            throw new IllegalArgumentException("Invalid name.");

        User user = findById(userId);
        user.setName(newName);
        userRepository.update(user);

        System.out.println("  ✔ Name updated to: " + newName);
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = findById(userId);

        if (!user.getPassword().equals(oldPassword))
            throw new AuthenticationException("Old password is incorrect.");
        if (!InputValidator.isValidPassword(newPassword))
            throw new IllegalArgumentException("Password must be at least 6 characters.");

        user.setPassword(newPassword);
        userRepository.update(user);
        System.out.println("  ✔ Password changed successfully.");
    }

    public void deleteUser(String userId) {
        findById(userId);                 // throws if not found
        userRepository.delete(userId);

        System.out.println("  ✔ User deleted: " + userId);
    }

}
