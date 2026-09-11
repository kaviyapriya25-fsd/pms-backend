package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Company;
import com.example.demo.entity.Employee;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.TeamLeaderRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           TeamLeaderRepository teamLeaderRepository,
                           UserRepository userRepository,
                           CompanyRepository companyRepository,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {

        this.employeeRepository = employeeRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees(Integer companyId) {
        if (companyId == null) {
            return List.of();
        }
        return employeeRepository.findByCompany_Id(companyId);
    }

    public Employee addEmployee(Employee employee) {

        if (employee.getEmail() != null && isEmailInUse(employee.getEmail())) {
            throw new DuplicateResourceException("Email '" + employee.getEmail() + "' is already registered in the system.");
        }

        if (employee.getCompany() == null || employee.getCompany().getId() == null) {
            throw new BadRequestException("Company ID is required.");
        }

        Company company = companyRepository
                .findById(employee.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", employee.getCompany().getId()));

        employee.setCompany(company);

        // RULE: Auto-inherit project from Team Leader if assigned; otherwise empty
        if (employee.getTeamLeader() != null && !employee.getTeamLeader().isBlank()) {
            teamLeaderRepository.findByEmail(employee.getTeamLeader())
                    .or(() -> teamLeaderRepository.findByName(employee.getTeamLeader()))
                    .ifPresent(tl -> {
                        employee.setProject(tl.getProject() != null ? tl.getProject() : "");
                    });
        } else {
            employee.setTeamLeader("");
            employee.setProject("");
        }

        // Auto-generate random secure password if not specified by Admin
        if (employee.getPassword() == null || employee.getPassword().isBlank()) {
            String randomTempPass = java.util.UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
            employee.setPassword(passwordEncoder.encode(randomTempPass));
        } else if (!employee.getPassword().startsWith("$2a$") && !employee.getPassword().startsWith("$2b$")) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Successfully added employee: {}", savedEmployee.getEmail());

        // Sync with User table
        User user = new User();
        user.setName(savedEmployee.getName());
        user.setEmail(savedEmployee.getEmail());
        user.setPassword(savedEmployee.getPassword());
        user.setRole("EMPLOYEE");
        user.setCompanyId(company.getId());
        user.setPhone(savedEmployee.getPhone());
        user.setDesignation(savedEmployee.getDesignation());
        user.setProject(savedEmployee.getProject());
        user.setStatus(savedEmployee.getStatus() != null ? savedEmployee.getStatus() : "ACTIVE");

        userRepository.save(user);

        // Trigger Account Provisioned Email to Employee's real Gmail
        try {
            emailService.sendMemberCreatedEmail(savedEmployee.getEmail(), savedEmployee.getName(), "EMPLOYEE");
        } catch (Exception e) {
            log.error("Employee Invitation mail error: {}", e.getMessage());
        }

        return savedEmployee;
    }

    public Employee updateEmployee(Integer id, Employee employee) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        existing.setName(employee.getName());
        existing.setPhone(employee.getPhone());
        existing.setDesignation(employee.getDesignation());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setStatus(employee.getStatus() != null ? employee.getStatus() : "ACTIVE");

        // RULE: Auto-inherit project from Team Leader if assigned; otherwise empty
        if (employee.getTeamLeader() != null && !employee.getTeamLeader().isBlank()) {
            existing.setTeamLeader(employee.getTeamLeader());
            teamLeaderRepository.findByEmail(employee.getTeamLeader())
                    .or(() -> teamLeaderRepository.findByName(employee.getTeamLeader()))
                    .ifPresent(tl -> {
                        existing.setProject(tl.getProject() != null ? tl.getProject() : "");
                    });
        } else {
            existing.setTeamLeader("");
            existing.setProject("");
        }

        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            String encoded = passwordEncoder.encode(employee.getPassword());
            existing.setPassword(encoded);
        }

        Employee updated = employeeRepository.save(existing);
        log.info("Successfully updated employee ID: {}", updated.getId());

        // Sync updates with User table
        userRepository.findByEmail(updated.getEmail())
                .ifPresent(user -> {
                    user.setName(updated.getName());
                    user.setPhone(updated.getPhone());
                    user.setDesignation(updated.getDesignation());
                    user.setProject(updated.getProject());
                    user.setStatus(updated.getStatus());

                    if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
                        user.setPassword(updated.getPassword());
                    }

                    userRepository.save(user);
                });

        return updated;
    }

    public void deleteEmployee(Integer id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        userRepository.findByEmail(employee.getEmail())
                .ifPresent(userRepository::delete);

        employeeRepository.delete(employee);
        log.info("Successfully deleted employee ID: {}", id);
    }

    private boolean isEmailInUse(String email) {
        if (email == null || email.isBlank()) return false;
        String clean = email.trim();
        return userRepository.existsByEmail(clean) ||
               teamLeaderRepository.existsByEmail(clean) ||
               employeeRepository.existsByEmail(clean) ||
               adminRepository.existsByAdminEmail(clean) ||
               companyRepository.existsByCompanyEmail(clean);
    }

}