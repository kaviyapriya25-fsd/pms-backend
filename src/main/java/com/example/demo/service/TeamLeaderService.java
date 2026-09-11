package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Company;
import com.example.demo.entity.TeamLeader;
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
public class TeamLeaderService {

    private static final Logger log = LoggerFactory.getLogger(TeamLeaderService.class);

    private final TeamLeaderRepository teamLeaderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public TeamLeaderService(
            TeamLeaderRepository teamLeaderRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            AdminRepository adminRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.teamLeaderRepository = teamLeaderRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<TeamLeader> getAllTeamLeaders(Integer companyId) {
        if (companyId == null) {
            return List.of();
        }
        return teamLeaderRepository.findByCompany_Id(companyId);
    }

    public TeamLeader addTeamLeader(TeamLeader leader) {

        if (leader.getEmail() != null && isEmailInUse(leader.getEmail())) {
            throw new DuplicateResourceException("Email '" + leader.getEmail() + "' is already registered in the system.");
        }

        if (leader.getCompany() == null || leader.getCompany().getId() == null) {
            throw new BadRequestException("Company ID is required.");
        }

        Company company = companyRepository
                .findById(leader.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", leader.getCompany().getId()));

        leader.setCompany(company);

        // RULE: Project MUST remain empty on creation — assigned ONLY when Admin creates a Project
        leader.setProject("");

        // Auto-generate random secure password if not specified by Admin
        if (leader.getPassword() == null || leader.getPassword().isBlank()) {
            String randomTempPass = java.util.UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
            leader.setPassword(passwordEncoder.encode(randomTempPass));
        } else if (!leader.getPassword().startsWith("$2a$") && !leader.getPassword().startsWith("$2b$")) {
            leader.setPassword(passwordEncoder.encode(leader.getPassword()));
        }

        TeamLeader savedLeader = teamLeaderRepository.save(leader);
        log.info("Successfully added team leader: {}", savedLeader.getEmail());

        // Sync with User table
        User user = new User();
        user.setName(savedLeader.getName());
        user.setEmail(savedLeader.getEmail());
        user.setPassword(savedLeader.getPassword());
        user.setRole("TEAM_LEADER");
        user.setCompanyId(company.getId());
        user.setPhone(savedLeader.getPhone());
        user.setDepartment(savedLeader.getDepartment());
        user.setProject("");
        user.setStatus(savedLeader.getStatus() != null ? savedLeader.getStatus() : "ACTIVE");

        userRepository.save(user);

        // Trigger Account Provisioned Email to Team Leader's real Gmail
        try {
            emailService.sendMemberCreatedEmail(savedLeader.getEmail(), savedLeader.getName(), "TEAM_LEADER");
        } catch (Exception e) {
            log.error("Team Leader Invitation mail error: {}", e.getMessage());
        }

        return savedLeader;
    }

    public TeamLeader updateTeamLeader(Integer id, TeamLeader leader) {

        TeamLeader existing = teamLeaderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamLeader", "id", id));

        existing.setName(leader.getName());
        existing.setPhone(leader.getPhone());
        existing.setDepartment(leader.getDepartment());
        existing.setStatus(leader.getStatus() != null ? leader.getStatus() : "ACTIVE");
        existing.setJoiningDate(leader.getJoiningDate());

        // Keep existing project — project is NOT changed from TeamLeader edit form
        if (leader.getPassword() != null && !leader.getPassword().isBlank()) {
            String encoded = passwordEncoder.encode(leader.getPassword());
            existing.setPassword(encoded);
        }

        TeamLeader updated = teamLeaderRepository.save(existing);
        log.info("Successfully updated team leader ID: {}", updated.getId());

        // Sync updates with User table
        userRepository.findByEmail(updated.getEmail())
                .ifPresent(user -> {
                    user.setName(updated.getName());
                    user.setPhone(updated.getPhone());
                    user.setDepartment(updated.getDepartment());
                    user.setStatus(updated.getStatus());

                    if (leader.getPassword() != null && !leader.getPassword().isBlank()) {
                        user.setPassword(updated.getPassword());
                    }

                    userRepository.save(user);
                });

        return updated;
    }

    public void deleteTeamLeader(Integer id) {

        TeamLeader leader = teamLeaderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamLeader", "id", id));

        userRepository.findByEmail(leader.getEmail())
                .ifPresent(userRepository::delete);

        teamLeaderRepository.delete(leader);
        log.info("Successfully deleted team leader ID: {}", id);
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