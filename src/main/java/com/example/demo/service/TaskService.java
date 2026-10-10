package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.entity.Project;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorklogRepository;

@Service
@Transactional
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final WorklogRepository worklogRepository;
    private final ProjectRepository projectRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public TaskService(TaskRepository repository,
                       EmployeeRepository employeeRepository,
                       UserRepository userRepository,
                       WorklogRepository worklogRepository,
                       ProjectRepository projectRepository,
                       EmailService emailService,
                       NotificationService notificationService) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.worklogRepository = worklogRepository;
        this.projectRepository = projectRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    public Task saveTask(Task task) {

        if (task.getStartDate() != null && task.getDueDate() != null) {
            if (task.getDueDate().isBefore(task.getStartDate())) {
                throw new IllegalArgumentException("Task Due Date cannot be before Start Date.");
            }
        }

        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("TODO");
        }

        if (task.getPriority() == null || task.getPriority().isBlank()) {
            task.setPriority("MEDIUM");
        }

        Task saved = repository.save(task);
        log.info("Successfully created task ID: {}", saved.getId());

        // Send Task Assigned Notification Email
        sendTaskAssignedNotification(saved);

        return saved;
    }

    public Task updateTask(Integer id, Task task) {

        if (task.getStartDate() != null && task.getDueDate() != null) {
            if (task.getDueDate().isBefore(task.getStartDate())) {
                throw new IllegalArgumentException("Task Due Date cannot be before Start Date.");
            }
        }

        Task existing = getTaskById(id);

        boolean assignedEmployeeChanged = task.getEmployee() != null && !task.getEmployee().equals(existing.getEmployee());

        existing.setTaskName(task.getTaskName());
        existing.setDescription(task.getDescription());
        existing.setProject(task.getProject());
        existing.setTeamLeader(task.getTeamLeader());
        existing.setEmployee(task.getEmployee());
        existing.setPriority(task.getPriority());
        existing.setStatus(task.getStatus());
        existing.setStartDate(task.getStartDate());
        existing.setDueDate(task.getDueDate());

        // Preserve optional fields that were missing before
        if (task.getComments() != null) existing.setComments(task.getComments());
        if (task.getWorkSubmission() != null) existing.setWorkSubmission(task.getWorkSubmission());
        if (task.getEstimatedHours() != null) existing.setEstimatedHours(task.getEstimatedHours());
        if (task.getLoggedHours() != null) existing.setLoggedHours(task.getLoggedHours());
        if (task.getStoryPoints() != null) existing.setStoryPoints(task.getStoryPoints());
        if (task.getSprintName() != null) existing.setSprintName(task.getSprintName());

        if (task.getBackendCompleted() != null) existing.setBackendCompleted(task.getBackendCompleted());
        if (task.getFrontendCompleted() != null) existing.setFrontendCompleted(task.getFrontendCompleted());
        if (task.getIntegrationCompleted() != null) existing.setIntegrationCompleted(task.getIntegrationCompleted());

        Task updated = repository.save(existing);
        log.info("Successfully updated task ID: {}", updated.getId());

        if (assignedEmployeeChanged) {
            sendTaskAssignedNotification(updated);
        }

        return updated;
    }

    /**
     * Dedicated method to append a comment to a task.
     * Directly manipulates only the comments field so no other field is lost.
     */
    public Task addComment(Integer id, String comment, String author) {
        Task task = getTaskById(id);
        String current = task.getComments() != null ? task.getComments() : "";
        String time = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String entry = "[" + time + "] " + (author != null ? author : "User") + ": " + comment;
        task.setComments(current.isBlank() ? entry : current + "\n" + entry);
        Task saved = repository.save(task);
        log.info("Comment added to task ID: {} by {}", id, author);
        return saved;
    }

    private void sendTaskAssignedNotification(Task task) {
        if (task == null || task.getEmployee() == null || task.getEmployee().isBlank()) return;

        try {
            String empInput = task.getEmployee();
            String empEmail = empInput;
            String empName = "Team Member";

            var optEmp = employeeRepository.findByEmail(empInput)
                    .or(() -> employeeRepository.findByName(empInput));

            if (optEmp.isPresent()) {
                Employee emp = optEmp.get();
                empEmail = emp.getEmail();
                empName = emp.getName();
            } else {
                var optUser = userRepository.findByEmail(empInput);
                if (optUser.isPresent()) {
                    User u = optUser.get();
                    empEmail = u.getEmail();
                    empName = u.getName();
                }
            }

            String dueDateStr = task.getDueDate() != null ? task.getDueDate().toString() : "Not Specified";

            emailService.sendTaskAssignedEmail(
                    empEmail,
                    empName,
                    task.getTaskName(),
                    task.getProject(),
                    task.getPriority(),
                    dueDateStr,
                    task.getDescription()
            );

            // Also create in-app notification
            notificationService.notifyTaskAssigned(task);
        } catch (Exception e) {
            log.error("Failed to send task notification email/in-app alert: {}", e.getMessage());
        }
    }

    public void deleteTask(Integer id) {
        Task task = getTaskById(id);
        repository.delete(task);
        log.info("Successfully deleted task ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<Task> getEmployeeTasks(String email) {
        return repository.findByEmployee(email);
    }

    @Transactional(readOnly = true)
    public List<Task> getLeaderTasks(String email) {
        return repository.findByTeamLeader(email);
    }

    @Transactional(readOnly = true)
    public List<Task> getProjectTasks(String project) {
        return repository.findByProject(project);
    }

    @Transactional(readOnly = true)
    public List<Task> getCompanyTasks(Integer companyId) {
        return repository.findByCompany_Id(companyId);
    }

    @Transactional(readOnly = true)
    public Page<Task> getCompanyTasks(Integer companyId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.searchTasks(companyId, search.trim(), pageable);
        }
        return repository.findByCompany_Id(companyId, pageable);
    }

    public Task updateTaskStatus(Integer id, String newStatus) {
        Task task = getTaskById(id);
        String upperStatus = newStatus.toUpperCase();
        task.setStatus(upperStatus);
        
        if ("COMPLETED".equalsIgnoreCase(upperStatus)) {
            task.setBackendCompleted(true);
            task.setFrontendCompleted(true);
            task.setIntegrationCompleted(true);
        } else if ("TODO".equalsIgnoreCase(upperStatus)) {
            task.setBackendCompleted(false);
            task.setFrontendCompleted(false);
            task.setIntegrationCompleted(false);
        }
        
        log.info("Updated task ID {} status to {}", id, upperStatus);
        Task saved = repository.save(task);
        syncProjectCompletionStatus(saved.getProject());
        return saved;
    }

    public Task updateTaskChecklist(Integer id, Boolean backendCompleted, Boolean frontendCompleted, Boolean integrationCompleted) {
        Task task = getTaskById(id);
        if (backendCompleted != null) task.setBackendCompleted(backendCompleted);
        if (frontendCompleted != null) task.setFrontendCompleted(frontendCompleted);
        if (integrationCompleted != null) task.setIntegrationCompleted(integrationCompleted);
        log.info("Updated technical checklist for task ID: {}", id);
        Task saved = repository.save(task);
        syncProjectCompletionStatus(saved.getProject());
        return saved;
    }

    public void syncProjectCompletionStatus(String projectName) {
        if (projectName == null || projectName.isBlank()) return;
        try {
            List<Project> projects = projectRepository.findAll();
            for (Project project : projects) {
                if (projectName.equalsIgnoreCase(project.getProjectName())) {
                    List<Task> tasks = repository.findByProject(projectName);
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
                                log.info("Auto-updated Project '{}' status to COMPLETED as all tasks are completed", projectName);
                            }
                        } else {
                            if (!"IN_PROGRESS".equalsIgnoreCase(project.getStatus())) {
                                project.setStatus("IN_PROGRESS");
                                projectRepository.save(project);
                                log.info("Reverted Project '{}' status to IN_PROGRESS as task(s) require progress/revision", projectName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error syncing project completion for '{}': {}", projectName, e.getMessage());
        }
    }

    public com.example.demo.entity.Worklog logWork(Integer taskId, Double hoursSpent, String userEmail, String userName, String description) {
        Task task = getTaskById(taskId);
        
        Double currentLogged = task.getLoggedHours() != null ? task.getLoggedHours() : 0.0;
        task.setLoggedHours(currentLogged + hoursSpent);
        repository.save(task);

        com.example.demo.entity.Worklog logEntry = new com.example.demo.entity.Worklog(taskId, userEmail, userName, hoursSpent, description);
        com.example.demo.entity.Worklog savedWorklog = worklogRepository.save(logEntry);
        notificationService.notifyWorklogLogged(task, userEmail, userName, hoursSpent);
        return savedWorklog;
    }

    @Transactional(readOnly = true)
    public List<com.example.demo.entity.Worklog> getTaskWorklogs(Integer taskId) {
        return worklogRepository.findByTaskIdOrderByLoggedAtDesc(taskId);
    }
}