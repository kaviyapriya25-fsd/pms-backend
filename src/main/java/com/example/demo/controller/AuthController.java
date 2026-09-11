package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.OtpRequest;
import com.example.demo.dto.RegisterCompanyRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;

    public AuthController(AuthService authService, UserRepository userRepository,
            AdminRepository adminRepository, CompanyRepository companyRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.companyRepository = companyRepository;
    }

    // 1. Register Company + Admin
    @PostMapping("/register-company")
    public ResponseEntity<String> registerCompany(
            @RequestBody RegisterCompanyRequest request) {

        String result = authService.registerCompany(request);
        if (result.startsWith("Company Registered")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    // 1b. Send Registration Email OTP
    @PostMapping("/send-registration-otp")
    public ResponseEntity<String> sendRegistrationOtp(
            @RequestBody ForgotPasswordRequest request) {

        String response = authService.sendRegistrationOtp(request.getEmail());
        return ResponseEntity.ok(response);
    }

    // 1c. Verify Registration Email OTP
    @PostMapping("/verify-registration-otp")
    public ResponseEntity<String> verifyRegistrationOtp(
            @RequestBody OtpRequest request) {

        boolean verified = authService.verifyRegistrationOtp(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok("OTP Verified Successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or Expired OTP");
    }

    // 2. Universal Login (Admin, Team Leader, Employee)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 2b. Google Single Sign-On (SSO) Login
    @PostMapping("/google-login")
    public ResponseEntity<LoginResponse> googleLogin(
            @RequestBody com.example.demo.dto.GoogleLoginRequest request) {

        LoginResponse response = authService.googleLogin(request);
        return ResponseEntity.ok(response);
    }

    // 3. Forgot Password -> Generate OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        String response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    // 3b. Send OTP (Alias for forgot-password used by frontend)
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestBody ForgotPasswordRequest request) {

        String response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    // 3c. Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @RequestBody com.example.demo.dto.ResendOtpRequest request) {

        String response = authService.resendOtp(request);
        return ResponseEntity.ok(response);
    }

    // 4. Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody OtpRequest request) {

        boolean verified = authService.verifyOtp(request);
        if (verified) {
            return ResponseEntity.ok("OTP Verified Successfully");
        }
        return ResponseEntity.badRequest().body("Invalid or Expired OTP");
    }

    // 5. Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        String response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    // CLEANUP: Remove corrupt null-email records from broken registrations
    @Transactional
    @DeleteMapping("/cleanup-broken-registrations")
    public ResponseEntity<String> cleanupBrokenRegistrations() {
        int usersDeleted = userRepository.deleteByEmailIsNullOrEmailIsEmpty();
        int adminsDeleted = adminRepository.deleteByAdminEmailIsNullOrAdminEmailIsEmpty();
        int companiesDeleted = companyRepository.deleteByCompanyEmailIsNullOrCompanyEmailIsEmpty();
        String msg = String.format(
            "Cleanup complete: %d users, %d admins, %d companies removed with null/empty emails.",
            usersDeleted, adminsDeleted, companiesDeleted
        );
        return ResponseEntity.ok(msg);
    }
}