package com.Sharvari.elearningPlatform.util;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidName(String name) {
        return name != null && !name.isBlank() && name.length() >= 2;
    }

    public static boolean isPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isValidPercentage(double value) {
        return value >= 0.0 && value <= 100.0;
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

}
