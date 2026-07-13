package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RegisterCompanyRequest;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-company")
    public String registerCompany(
            @RequestBody RegisterCompanyRequest request) {

        return authService.registerCompany(request);

    }

}