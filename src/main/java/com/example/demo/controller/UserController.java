package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    private final JwtService jwtService;

    public UserController(UserRepository userRepository,
                          UserService userService,
                          JwtService jwtService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody User user) {

        User loggedUser = userService.login(
                user.getEmail(),
                user.getPassword());

        if (loggedUser != null) {

            String token = jwtService.generateToken(
                    loggedUser.getEmail());

            return new LoginResponse(
                    token,
                    loggedUser.getName(),
                    loggedUser.getRole());
        }

        return null;
    }
}