package com.Sharvari.elearningPlatform.service;

import com.Sharvari.elearningPlatform.exception.AuthenticationException;
import com.Sharvari.elearningPlatform.exception.UserNotFoundException;
import com.Sharvari.elearningPlatform.model.Instructor;
import com.Sharvari.elearningPlatform.model.Student;
import com.Sharvari.elearningPlatform.model.User;
import com.Sharvari.elearningPlatform.util.IdGenerator;
import com.Sharvari.elearningPlatform.util.InputValidator;

import java.util.*;

public class UserService {

    private final Map<String, User> usersById    = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    public Student registerStudent(String name, String email, String password) {
        validateRegistrationInput(name, email, password);
        String id = IdGenerator.generateUserId();
        Student student = new Student(id, name, email, password);
        usersById.put(id, student);
        usersByEmail.put(email.toLowerCase(), student);
        System.out.println("  ✔ Student registered! ID: " + id);
        return student;
    }

    public Instructor registerInstructor(String name, String email, String password, String expertise) {
        validateRegistrationInput(name, email, password);
        if (!InputValidator.isNotBlank(expertise)) throw new IllegalArgumentException("Expertise cannot be blank.");
        String id = IdGenerator.generateUserId();
        Instructor instructor = new Instructor(id, name, email, password, expertise);
        usersById.put(id, instructor);
        usersByEmail.put(email.toLowerCase(), instructor);
        System.out.println("  ✔ Instructor registered! ID: " + id);
        return instructor;
    }


    private void validateRegistrationInput(String name, String email, String password) {
        if (!InputValidator.isValidName(name))       throw new IllegalArgumentException("Name must be at least 2 characters.");
        if (!InputValidator.isValidEmail(email))     throw new IllegalArgumentException("Invalid email format.");
        if (usersByEmail.containsKey(email.toLowerCase())) throw new IllegalArgumentException("Email already registered.");
        if (!InputValidator.isValidPassword(password)) throw new IllegalArgumentException("Password must be at least 6 characters.");
    }

    public User login(String email, String password) {
        User user = usersByEmail.get(email.toLowerCase());
        if (user == null || !user.getPassword().equals(password))
            throw new AuthenticationException("Invalid email or password.");
        System.out.println("  ✔ Login successful! Welcome, " + user.getName() + ".");
        return user;
    }


    public User findById(String userId) {
        User user = usersById.get(userId);
        if (user == null) throw new UserNotFoundException("User not found: " + userId);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }

    public List<User> getAllUsers(){
        return new ArrayList<>(usersById.values());
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        for (User u : usersById.values()) if (u instanceof Student) list.add((Student) u);
        return list;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        for (User u : usersById.values()) if (u instanceof Instructor) list.add((Instructor) u);
        return list;
    }

    public void updateUserName(String userId, String newName) {
        if (!InputValidator.isValidName(newName)) throw new IllegalArgumentException("Invalid name.");
        findById(userId).setName(newName);
        System.out.println("  ✔ Name updated to: " + newName);
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        if (!user.getPassword().equals(oldPassword))
            throw new AuthenticationException("Old password is incorrect.");
        if (!InputValidator.isValidPassword(newPassword))
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        user.setPassword(newPassword);
        System.out.println("  ✔ Password changed successfully.");
    }

    public void deleteUser(String userId) {
        User user = findById(userId);
        usersById.remove(userId);
        usersByEmail.remove(user.getEmail().toLowerCase());
        System.out.println("  ✔ User deleted: " + userId);
    }

    public void loadDemoData() {
        registerStudent("Alice Johnson",  "alice@email.com", "alice123");
        registerStudent("Bob Smith",      "bob@email.com",   "bob123");
        registerInstructor("Dr. Sarah Lee",   "sarah@email.com", "sarah123", "Computer Science");
        registerInstructor("Prof. Mark Roy",  "mark@email.com",  "mark123",  "Data Science");
    }



}
