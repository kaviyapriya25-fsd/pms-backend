package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    public UserController(
            UserRepository userRepository,
            UserService userService,
            AuthService authService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }

    // ===========================
    // Get All Users
    // ===========================

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===========================
    // Register User
    // ===========================

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // ===========================
    // Login
    // ===========================

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}