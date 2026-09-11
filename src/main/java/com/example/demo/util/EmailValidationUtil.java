package com.example.demo.util;

import java.util.regex.Pattern;

public class EmailValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern OTP_PATTERN = Pattern.compile(
            "^\\d{6}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidOtp(String otp) {
        if (otp == null || otp.isBlank()) {
            return false;
        }
        return OTP_PATTERN.matcher(otp.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        return password.trim().length() >= 4;
    }

    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }

    public static void validateOtp(String otp) {
        if (!isValidOtp(otp)) {
            throw new IllegalArgumentException("OTP must be a valid 6-digit numeric code.");
        }
    }

    public static void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
    }
}
