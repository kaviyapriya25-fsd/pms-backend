package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Project;
import com.example.demo.entity.TeamLeader;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.TeamLeaderRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                          TeamLeaderRepository teamLeaderRepository,
                          EmployeeRepository employeeRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository,
                          EmailService emailService,
                          NotificationService notificationService) {

        this.projectRepository = projectRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // ======================
    // Get All
    // ======================

    @Transactional
    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        projects.forEach(this::syncProjectCompletionStatus);
        return projects;
    }

    @Transactional
    public List<Project> getProjectsByCompany(Integer companyId) {
        List<Project> projects = projectRepository.findByCompany_Id(companyId);
        projects.forEach(this::syncProjectCompletionStatus);
        return projects;
    }

    @Transactional
    public Page<Project> getProjectsByCompany(Integer companyId, String search, Pageable pageable) {
        Page<Project> page;
        if (search != null && !search.isBlank()) {
            page = projectRepository.searchProjects(companyId, search.trim(), pageable);
        } else {
            page = projectRepository.findByCompany_Id(companyId, pageable);
        }
        page.getContent().forEach(this::syncProjectCompletionStatus);
        return page;
    }

    public void syncProjectCompletionStatus(Project project) {
        if (project == null || project.getProjectName() == null || project.getProjectName().isBlank()) return;
        try {
            List<com.example.demo.entity.Task> tasks = taskRepository.findByProject(project.getProjectName());
            if (tasks != null && !tasks.isEmpty()) {
                boolean allDone = tasks.stream().allMatch(t ->
                        "COMPLETED".equalsIgnoreCase(t.getStatus()) ||
                        "REVIEW".equalsIgnoreCase(t.getStatus())
                );
                boolean hasPendingWork = tasks.stream().anyMatch(t ->
                        "IN_PROGRESS".equalsIgnoreCase(t.getStatus()) ||
                        "IN_TESTING".equalsIgnoreCase(t.getStatus()) ||
                        "BLOCKED".equalsIgnoreCase(t.getStatus()) ||
                        "TODO".equalsIgnoreCase(t.getStatus())
                );

                if (allDone && !hasPendingWork) {
                    if (!"COMPLETED".equalsIgnoreCase(project.getStatus())) {
                        project.setStatus("COMPLETED");
                        projectRepository.save(project);
                        log.info("Auto-updated Project '{}' status to COMPLETED as all tasks are completed.", project.getProjectName());
                    }
                } else {
                    if (!"IN_PROGRESS".equalsIgnoreCase(project.getStatus())) {
                        project.setStatus("IN_PROGRESS");
                        projectRepository.save(project);
                        log.info("Reverted Project '{}' status to IN_PROGRESS as task(s) require progress/revision.", project.getProjectName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to sync project status: {}", e.getMessage());
        }
    }

    // ======================
    // Get By Id
    // ======================

    @Transactional(readOnly = true)
    public Project getProjectById(Integer id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", "id", id));

    }

    // ======================
    // Save
    // ======================

    public Project saveProject(Project project) {

        Project saved = projectRepository.save(project);
        log.info("Successfully saved new project: {}", saved.getProjectName());

        // Assign project to team leader and all employees under that leader
        assignProject(saved.getLeader(), saved.getProjectName());

        // Dispatch Project Assigned HTML Email to Leader
        sendProjectAssignedNotification(saved);

        return saved;

    }

    // ======================
    // Update
    // ======================

    public Project updateProject(Integer id, Project project) {

        Project existing = getProjectById(id);

        // If leader changed, clear project from old leader and their employees
        if (existing.getLeader() != null && !existing.getLeader().equals(project.getLeader())) {
            clearProject(existing.getLeader());
        }

        existing.setProjectName(project.getProjectName());
        existing.setDescription(project.getDescription());
        existing.setLeader(project.getLeader());
        existing.setPriority(project.getPriority());
        existing.setStatus(project.getStatus());
        existing.setStartDate(project.getStartDate());
        existing.setEndDate(project.getEndDate());

        // Only update company if provided — never overwrite with null
        if (project.getCompany() != null && project.getCompany().getId() != null) {
            existing.setCompany(project.getCompany());
        }

        Project updated = projectRepository.save(existing);
        log.info("Successfully updated project ID: {}", updated.getId());

        // Re-assign project to new leader and their employees
        assignProject(updated.getLeader(), updated.getProjectName());

        // Dispatch Project Assigned HTML Email to Leader
        sendProjectAssignedNotification(updated);

        return updated;
    }

    private void sendProjectAssignedNotification(Project project) {
        if (project == null || project.getLeader() == null || project.getLeader().isBlank()) return;

        try {
            // Leader field could be leader email or name
            String leaderInput = project.getLeader();
            String leaderEmail = leaderInput;
            String leaderName = "Team Leader";

            var optionalLeader = teamLeaderRepository.findByEmail(leaderInput)
                    .or(() -> teamLeaderRepository.findByName(leaderInput));

            if (optionalLeader.isPresent()) {
                TeamLeader tl = optionalLeader.get();
                leaderEmail = tl.getEmail();
                leaderName = tl.getName();
            }

            String startDateStr = project.getStartDate() != null ? project.getStartDate().toString() : "TBD";
            String endDateStr = project.getEndDate() != null ? project.getEndDate().toString() : "TBD";

            emailService.sendProjectAssignedEmail(
                    leaderEmail,
                    leaderName,
                    project.getProjectName(),
                    project.getDescription(),
                    project.getPriority(),
                    startDateStr,
                    endDateStr
            );

            // Also create in-app notification
            notificationService.notifyProjectAssigned(project);
        } catch (Exception e) {
            log.error("Failed to send project assignment email/in-app alert: {}", e.getMessage());
        }
    }

    // ======================
    // Delete
    // ======================

    public void deleteProject(Integer id) {

        Project project = getProjectById(id);

        // Clear project from team leader and all employees under that leader
        clearProject(project.getLeader());

        projectRepository.delete(project);
        log.info("Successfully deleted project ID: {}", id);

    }

    // ======================
    // Helper: Assign Project
    // ======================

    private void assignProject(String leaderEmail, String projectName) {

        if (leaderEmail == null || leaderEmail.isBlank()) return;

        // 1. Update TeamLeader entity
        teamLeaderRepository.findByEmail(leaderEmail)
                .or(() -> teamLeaderRepository.findByName(leaderEmail))
                .ifPresent(leader -> {

                    leader.setProject(projectName);
                    teamLeaderRepository.save(leader);

                    // 2. Update all Employees under this team leader
                    updateEmployeesProject(leader.getName(), projectName);

                });

        // 3. Update User entity for the leader
        userRepository.findByEmail(leaderEmail)
                .ifPresent(user -> {

                    user.setProject(projectName);
                    userRepository.save(user);

                });

    }

    // ======================
    // Helper: Clear Project
    // ======================

    private void clearProject(String leaderEmail) {

        if (leaderEmail == null || leaderEmail.isBlank()) return;

        // 1. Update TeamLeader entity
        teamLeaderRepository.findByEmail(leaderEmail)
                .or(() -> teamLeaderRepository.findByName(leaderEmail))
                .ifPresent(leader -> {

                    leader.setProject("");
                    teamLeaderRepository.save(leader);

                    // 2. Clear project for all employees under this team leader
                    updateEmployeesProject(leader.getName(), "");

                });

        // 3. Update User entity for the leader
        userRepository.findByEmail(leaderEmail)
                .ifPresent(user -> {

                    user.setProject("");
                    userRepository.save(user);

                });

    }

    // ======================
    // Helper: Update Employees
    // ======================

    private void updateEmployeesProject(String leaderName, String projectName) {

        if (leaderName == null || leaderName.isBlank()) return;

        // Find all employees whose teamLeader field matches this leader's name
        List<Employee> employees = employeeRepository.findByTeamLeader(leaderName);

        for (Employee emp : employees) {
            emp.setProject(projectName);
            employeeRepository.save(emp);
        }

    }

}