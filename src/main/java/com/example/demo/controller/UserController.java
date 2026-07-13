package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserRepository userRepository,
            UserService userService,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
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

        User loggedUser = userService.login(
                request.getEmail(),
                request.getPassword());

        if (loggedUser != null) {

            String token = jwtService.generateToken(
                    loggedUser.getEmail());

            return new LoginResponse(
                    true,
                    "Login Successful",
                    token,
                    loggedUser.getId(),
                    null, // companyId (we'll connect later)
                    loggedUser.getName(),
                    loggedUser.getRole()
            );
        }

        return new LoginResponse(
                false,
                "Invalid Email or Password",
                null,
                null,
                null,
                null,
                null
        );
    }
}