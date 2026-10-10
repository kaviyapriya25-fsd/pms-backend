package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.OtpRequest;
import com.example.demo.dto.RegisterCompanyRequest;
import com.example.demo.dto.ResendOtpRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Company;
import com.example.demo.entity.Employee;
import com.example.demo.entity.TeamLeader;
import com.example.demo.entity.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TeamLeaderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.util.EmailValidationUtil;

@Service
@Transactional
public class AuthService {

    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(
            CompanyRepository companyRepository,
            AdminRepository adminRepository,
            UserRepository userRepository,
            TeamLeaderRepository teamLeaderRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService) {

        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public static class RegistrationOtpEntry {
        public String otp;
        public LocalDateTime expiry;
        public boolean verified;
    }

    private final Map<String, RegistrationOtpEntry> registrationOtpMap = new java.util.concurrent.ConcurrentHashMap<>();

    // ==========================================
    // 1. REGISTER COMPANY + ADMIN
    // ==========================================

    public String sendRegistrationOtp(String email) {
        EmailValidationUtil.validateEmail(email);
        if (isEmailAlreadyRegistered(email)) {
            throw new RuntimeException("Email '" + email + "' is already registered in the system.");
        }

        String otp = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
        RegistrationOtpEntry entry = new RegistrationOtpEntry();
        entry.otp = otp;
        entry.expiry = LocalDateTime.now().plusMinutes(2);
        entry.verified = false;

        registrationOtpMap.put(email.toLowerCase().trim(), entry);

        System.out.println("=== REGISTRATION OTP for " + email + " is: " + otp + " ===");

        try {
            emailService.sendRegistrationOtpEmail(email, "Company Admin", otp, 2);
        } catch (Exception e) {
            System.err.println("[AuthService] Registration OTP email error: " + e.getMessage());
        }

        return "OTP sent successfully to " + email;
    }

    public boolean verifyRegistrationOtp(String email, String otp) {
        EmailValidationUtil.validateEmail(email);
        EmailValidationUtil.validateOtp(otp);

        RegistrationOtpEntry entry = registrationOtpMap.get(email.toLowerCase().trim());
        if (entry == null || !entry.otp.equals(otp)) {
            throw new RuntimeException("Invalid OTP code");
        }
        if (entry.expiry == null || entry.expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP code has expired. Please request a fresh OTP.");
        }

        entry.verified = true;
        return true;
    }

    public String registerCompany(RegisterCompanyRequest request) {

        EmailValidationUtil.validateEmail(request.getCompanyEmail());
        EmailValidationUtil.validateEmail(request.getAdminEmail());
        EmailValidationUtil.validatePassword(request.getPassword());

        if (isEmailAlreadyRegistered(request.getAdminEmail())) {
            return "Admin Email is already registered in the system";
        }

        // Mandatory Admin Email OTP verification check
        RegistrationOtpEntry otpEntry = registrationOtpMap.get(request.getAdminEmail().toLowerCase().trim());
        if (otpEntry == null || !otpEntry.verified) {
            return "Admin Email must be verified via OTP before registering to prevent email misuse.";
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Company company = companyRepository.findByCompanyEmail(request.getCompanyEmail().trim()).orElse(null);

        if (company == null) {
            company = new Company();
            company.setCompanyName(request.getCompanyName());
            company.setCompanyEmail(request.getCompanyEmail());
            company.setPhone(request.getPhone());
            company.setWebsite(request.getWebsite());
            company.setIndustry(request.getIndustry());
            company.setCompanySize(request.getCompanySize());
            company.setAddress(request.getAddress());
            company = companyRepository.save(company);
        } else {
            // Re-use existing company and update optional profile fields if supplied
            if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
                company.setCompanyName(request.getCompanyName());
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                company.setPhone(request.getPhone());
            }
            if (request.getWebsite() != null && !request.getWebsite().isBlank()) {
                company.setWebsite(request.getWebsite());
            }
            company = companyRepository.save(company);
        }

        Admin admin = new Admin();
        admin.setAdminName(request.getAdminName());
        admin.setAdminEmail(request.getAdminEmail());
        admin.setPassword(encodedPassword);
        admin.setCompany(company);

        adminRepository.save(admin);

        User user = new User();
        user.setName(request.getAdminName());
        user.setEmail(request.getAdminEmail());
        user.setPassword(encodedPassword);
        user.setRole("ADMIN");
        user.setCompanyId(company.getId());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE");

        userRepository.save(user);

        // Remove OTP record after successful registration
        registrationOtpMap.remove(request.getAdminEmail().toLowerCase().trim());

        // Trigger Welcome HTML Email to Admin
        try {
            emailService.sendWelcomeEmail(request.getAdminEmail(), request.getAdminName(), "ADMIN", "http://localhost:5173/login");
        } catch (Exception e) {
            System.err.println("[AuthService] Welcome email error: " + e.getMessage());
        }

        return "Company Registered Successfully";
    }

    // ==========================================
    // 2. LOGIN (Admin, Team Leader, Employee)
    // ==========================================

    public LoginResponse login(LoginRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());

        User user = findOrCreateUserByEmail(request.getEmail());

        if (!checkPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Email or Password");
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!isMatchingRole(request.getRole(), user.getRole())) {
                throw new RuntimeException("Invalid Role selection for this user. Registered as " + getReadableRoleName(user.getRole()));
            }
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("User account is Inactive");
        }

        // Generate real JWT token containing claims: userId, companyId, role, email
        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage("Login Successful");
        response.setToken(token);
        response.setUserId(user.getId());
        response.setCompanyId(user.getCompanyId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        // Trigger Login Security Alert Email with exact timestamp
        try {
            String loginTime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")) + " IST";
            emailService.sendLoginAlertEmail(user.getEmail(), user.getName(), user.getRole(), loginTime);
        } catch (Exception e) {
            System.err.println("[AuthService] Login alert email error: " + e.getMessage());
        }

        return response;
    }

    // ==========================================
    // 2b. GOOGLE SINGLE SIGN-ON (SSO) LOGIN
    // ==========================================

    public LoginResponse googleLogin(com.example.demo.dto.GoogleLoginRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());

        User user = findOrCreateUserByEmail(request.getEmail());

        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!isMatchingRole(request.getRole(), user.getRole())) {
                String registeredRoleName = getReadableRoleName(user.getRole());
                throw new RuntimeException("This Google account is registered as a " + registeredRoleName + 
                        ". Please use the " + registeredRoleName + " login portal.");
            }
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("User account is currently INACTIVE.");
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage("Google SSO Login Successful");
        response.setToken(token);
        response.setUserId(user.getId());
        response.setCompanyId(user.getCompanyId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        // Trigger Login Security Alert Email with exact timestamp
        try {
            String loginTime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")) + " IST";
            emailService.sendLoginAlertEmail(user.getEmail(), user.getName(), user.getRole(), loginTime);
        } catch (Exception e) {
            System.err.println("[AuthService] Google Login alert email error: " + e.getMessage());
        }

        return response;
    }

    // ==========================================
    // 3. FORGOT PASSWORD (Generate & Send OTP)
    // ==========================================

    public String forgotPassword(ForgotPasswordRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());

        // 1. Check if email exists in system
        User user = findOrCreateUserByEmail(request.getEmail());

        // 2. Role-Based Verification: Ensure user's role matches portal role requested
        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!isMatchingRole(request.getRole(), user.getRole())) {
                String registeredRoleName = getReadableRoleName(user.getRole());
                throw new RuntimeException("This email address belongs to a " + registeredRoleName + 
                        " account. Please use the " + registeredRoleName + " login portal to reset your password.");
            }
        }

        // 3. Account Status Check
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("This account is currently INACTIVE. Please contact your system administrator.");
        }

        // 4. Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));

        userRepository.save(user);

        System.out.println("=== OTP for " + request.getEmail() + " is: " + otp + " ===");

        // Send OTP HTML Email to the user's real email address (valid for 2 mins)
        emailService.sendOtpEmail(user.getEmail(), user.getName(), otp, 2);

        return "OTP verification code sent successfully to " + request.getEmail();
    }

    // ==========================================
    // 3b. RESEND OTP
    // ==========================================

    public String resendOtp(ResendOtpRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());

        User user = findOrCreateUserByEmail(request.getEmail());

        // Role-Based Verification
        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!isMatchingRole(request.getRole(), user.getRole())) {
                String registeredRoleName = getReadableRoleName(user.getRole());
                throw new RuntimeException("This email address belongs to a " + registeredRoleName + 
                        " account. Please use the " + registeredRoleName + " login portal.");
            }
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("This account is currently INACTIVE.");
        }

        // Generate fresh 6-digit OTP and reset 2-min expiry window
        String newOtp = String.format("%06d", new Random().nextInt(900000) + 100000);
        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));

        userRepository.save(user);

        System.out.println("=== RESENT OTP for " + request.getEmail() + " is: " + newOtp + " ===");

        // Dispatch updated OTP HTML Email to user's real email address (valid for 2 mins)
        emailService.sendOtpEmail(user.getEmail(), user.getName(), newOtp, 2);

        return "Fresh OTP code sent successfully to " + request.getEmail();
    }

    // ==========================================
    // 4. VERIFY OTP
    // ==========================================

    public boolean verifyOtp(OtpRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());
        EmailValidationUtil.validateOtp(request.getOtp());

        User user = findOrCreateUserByEmail(request.getEmail());

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP code");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new code.");
        }

        return true;
    }

    // ==========================================
    // 5. RESET PASSWORD
    // ==========================================

    public String resetPassword(ResetPasswordRequest request) {

        EmailValidationUtil.validateEmail(request.getEmail());
        EmailValidationUtil.validateOtp(request.getOtp());
        EmailValidationUtil.validatePassword(request.getNewPassword());

        User user = findOrCreateUserByEmail(request.getEmail());

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP code");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please click Resend OTP.");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // Update User
        user.setPassword(encodedPassword);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        // Update TeamLeader table if user is Team Leader
        teamLeaderRepository.findByEmail(user.getEmail()).ifPresent(tl -> {
            tl.setPassword(encodedPassword);
            teamLeaderRepository.save(tl);
        });

        // Update Employee table if user is Employee
        employeeRepository.findByEmail(user.getEmail()).ifPresent(emp -> {
            emp.setPassword(encodedPassword);
            employeeRepository.save(emp);
        });

        // Update Admin table if user is Admin
        Admin admin = adminRepository.findByAdminEmail(user.getEmail());
        if (admin != null) {
            admin.setPassword(encodedPassword);
            adminRepository.save(admin);
        }

        // Trigger Password Changed HTML Notification Email to user's real email address
        try {
            emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            System.err.println("[AuthService] Password changed email error: " + e.getMessage());
        }

        return "Password Reset Successfully. You can now login with your new password.";
    }

    // Helper: Find or auto-sync User from Admin, TeamLeader, or Employee tables
    private User findOrCreateUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user;
        }

        // Search Admin table
        Admin admin = adminRepository.findByAdminEmail(email);
        if (admin != null) {
            User newUser = new User();
            newUser.setName(admin.getAdminName());
            newUser.setEmail(admin.getAdminEmail());
            newUser.setPassword(admin.getPassword());
            newUser.setRole("ADMIN");
            if (admin.getCompany() != null) {
                newUser.setCompanyId(admin.getCompany().getId());
            }
            newUser.setStatus("ACTIVE");
            return userRepository.save(newUser);
        }

        // Search TeamLeader table
        TeamLeader tl = teamLeaderRepository.findByEmail(email).orElse(null);
        if (tl != null) {
            User newUser = new User();
            newUser.setName(tl.getName());
            newUser.setEmail(tl.getEmail());
            newUser.setPassword(tl.getPassword());
            newUser.setRole("TEAM_LEADER");
            if (tl.getCompany() != null) {
                newUser.setCompanyId(tl.getCompany().getId());
            }
            newUser.setPhone(tl.getPhone());
            newUser.setDepartment(tl.getDepartment());
            newUser.setProject(tl.getProject() != null ? tl.getProject() : "");
            newUser.setStatus(tl.getStatus() != null ? tl.getStatus() : "ACTIVE");
            return userRepository.save(newUser);
        }

        // Search Employee table
        Employee emp = employeeRepository.findByEmail(email).orElse(null);
        if (emp != null) {
            User newUser = new User();
            newUser.setName(emp.getName());
            newUser.setEmail(emp.getEmail());
            newUser.setPassword(emp.getPassword());
            newUser.setRole("EMPLOYEE");
            if (emp.getCompany() != null) {
                newUser.setCompanyId(emp.getCompany().getId());
            }
            newUser.setPhone(emp.getPhone());
            newUser.setDesignation(emp.getDesignation());
            newUser.setProject(emp.getProject() != null ? emp.getProject() : "");
            newUser.setStatus(emp.getStatus() != null ? emp.getStatus() : "ACTIVE");
            return userRepository.save(newUser);
        }

        throw new RuntimeException("Registered email not found in system");
    }

    // Helper: Check if email already exists across any table
    public boolean isEmailAlreadyRegistered(String email) {
        if (email == null || email.isBlank()) return false;
        String cleanEmail = email.trim();

        return userRepository.existsByEmail(cleanEmail) ||
               adminRepository.existsByAdminEmail(cleanEmail) ||
               teamLeaderRepository.existsByEmail(cleanEmail) ||
               employeeRepository.existsByEmail(cleanEmail) ||
               companyRepository.existsByCompanyEmail(cleanEmail);
    }

    // Helper: Human-readable role names
    private String getReadableRoleName(String role) {
        if (role == null) return "User";
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "Admin";
            case "TEAM_LEADER", "LEADER" -> "Team Leader";
            case "EMPLOYEE" -> "Employee";
            default -> role;
        };
    }

    // Helper: Flexible role matcher supporting synonyms (e.g. LEADER vs TEAM_LEADER)
    private boolean isMatchingRole(String requestedRole, String userRole) {
        if (requestedRole == null || requestedRole.isBlank()) return true;
        if (userRole == null) return false;

        String req = requestedRole.trim().toUpperCase();
        String user = userRole.trim().toUpperCase();

        if (req.equals(user)) return true;

        if ((req.equals("LEADER") || req.equals("TEAM_LEADER")) &&
            (user.equals("LEADER") || user.equals("TEAM_LEADER"))) {
            return true;
        }

        return false;
    }

    // Helper: Compare BCrypt or plain text password
    private boolean checkPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) return false;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }
}