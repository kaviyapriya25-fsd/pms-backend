package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Company;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Task;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskService taskService;
    private final SecurityUtil securityUtil;
    private final CompanyRepository companyRepository;
    private final NotificationRepository notificationRepository;

    public TaskController(TaskService taskService,
                          SecurityUtil securityUtil,
                          CompanyRepository companyRepository,
                          NotificationRepository notificationRepository) {

        this.taskService = taskService;
        this.securityUtil = securityUtil;
        this.companyRepository = companyRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Task> getAllTasks(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        if (activeCompanyId != null) {
            return taskService.getCompanyTasks(activeCompanyId);
        }
        return taskService.getAllTasks();
    }

    @GetMapping("/page")
    public ResponseEntity<com.example.demo.dto.PagedResponseDTO<Task>> getTasksPaged(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Task> result = taskService.getCompanyTasks(activeCompanyId, search, pageable);
        return ResponseEntity.ok(com.example.demo.dto.PagedResponseDTO.from(result));
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Integer id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task addTask(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Task task) {

        Integer activeCompanyId = securityUtil.extractCompanyId(
                authHeader,
                task.getCompany() != null ? task.getCompany().getId() : null
        );

        if (activeCompanyId != null) {
            Company company = companyRepository.findById(activeCompanyId).orElse(null);
            task.setCompany(company);
        }

        Task saved = taskService.saveTask(task);
        
        // Notify employee & team leader about new task
        if (saved.getCompany() != null) {
            String emp = saved.getEmployee() != null ? saved.getEmployee() : "Team Member";
            createNotification(saved.getCompany().getId(), "New Task Assigned: " + saved.getTaskName(),
                    "Task #" + saved.getId() + " (" + saved.getTaskName() + ") assigned to " + emp + " in project " + saved.getProject(),
                    "TASK", emp.contains("@") ? emp : null, "EMPLOYEE");
        }

        return saved;
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Integer id,
            @RequestBody Task task) {

        Task updated = taskService.updateTask(id, task);
        if (updated.getCompany() != null) {
            createNotification(updated.getCompany().getId(), "Task Updated: " + updated.getTaskName(),
                    "Task #" + updated.getId() + " (" + updated.getTaskName() + ") was updated.",
                    "TASK", null, "ALL");
        }
        return updated;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {

        String newStatus = payload.get("status");
        String requesterRole = payload.getOrDefault("requesterRole", "leader");

        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Employees can only move tasks forward — enforce server-side
        if ("employee".equalsIgnoreCase(requesterRole)) {
            java.util.List<String> order = java.util.List.of("TODO", "IN_PROGRESS", "IN_TESTING", "BLOCKED", "REVIEW", "COMPLETED");
            Task current = taskService.getTaskById(id);
            int currentIdx = order.indexOf(current.getStatus() != null ? current.getStatus().toUpperCase() : "TODO");
            int newIdx     = order.indexOf(newStatus.toUpperCase());
            if (newIdx < currentIdx) {
                return ResponseEntity.status(403)
                        .build(); // Employee cannot move tasks backward
            }
        }

        Task updated = taskService.updateTaskStatus(id, newStatus);

        if (updated.getCompany() != null) {
            createNotification(updated.getCompany().getId(), "Task Status Changed: " + updated.getTaskName(),
                    "Task #" + updated.getId() + " (" + updated.getTaskName() + ") status moved to " + newStatus,
                    "TASK", null, "ALL");
        }

        return ResponseEntity.ok(updated);
    }

    private void createNotification(Integer companyId, String title, String message, String type, String recipientEmail, String recipientRole) {
        if (companyId == null) return;
        try {
            Notification notif = new Notification();
            notif.setCompanyId(companyId);
            notif.setTitle(title);
            notif.setMessage(message);
            notif.setType(type);
            notif.setRecipientEmail(recipientEmail);
            notif.setRecipientRole(recipientRole);
            notificationRepository.save(notif);
        } catch (Exception e) {
            // Ignore notification error
        }
    }

    @PatchMapping("/{id}/checklist")
    public ResponseEntity<Task> updateTaskChecklist(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> payload) {

        Boolean backendCompleted = payload.get("backendCompleted");
        Boolean frontendCompleted = payload.get("frontendCompleted");
        Boolean integrationCompleted = payload.get("integrationCompleted");

        Task updated = taskService.updateTaskChecklist(id, backendCompleted, frontendCompleted, integrationCompleted);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/work")
    public ResponseEntity<Task> uploadWorkSubmission(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {

        String workSubmission = payload.get("workSubmission");
        Task existing = taskService.getTaskById(id);
        existing.setWorkSubmission(workSubmission);
        
        // Auto update status to REVIEW stage when deliverable is uploaded
        if (existing.getStatus() != null && ("TODO".equalsIgnoreCase(existing.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(existing.getStatus()))) {
            existing.setStatus("REVIEW");
        }

        Task updated = taskService.updateTask(id, existing);
        taskService.syncProjectCompletionStatus(updated.getProject());

        // Record a notification for company leaders/admin
        if (existing.getCompany() != null) {
            try {
                Notification notif = new Notification();
                notif.setCompanyId(existing.getCompany().getId());
                notif.setTitle("Deliverable Submitted");
                notif.setMessage("Employee " + (existing.getEmployee() != null ? existing.getEmployee() : "Team Member") + " submitted deliverables for task #" + existing.getId() + " (" + existing.getTaskName() + ")");
                notif.setType("DELIVERABLE");
                notificationRepository.save(notif);
            } catch (Exception e) {
                // Ignore notification save errors
            }
        }

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Task> addTaskComment(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {

        String comment = payload.get("comment");
        String author = payload.get("author");

        if (comment == null || comment.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Task updated = taskService.addComment(id, comment, author);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/leader/{email}")
    public List<Task> getLeaderTasks(@PathVariable String email) {
        return taskService.getLeaderTasks(email);
    }

    @GetMapping("/employee/{email}")
    public List<Task> getEmployeeTasks(@PathVariable String email) {
        return taskService.getEmployeeTasks(email);
    }

    @GetMapping("/project/{project}")
    public List<Task> getProjectTasks(@PathVariable String project) {
        return taskService.getProjectTasks(project);
    }

    @GetMapping("/company/{companyId}")
    public List<Task> getCompanyTasks(@PathVariable Integer companyId) {
        return taskService.getCompanyTasks(companyId);
    }

    @PostMapping("/{id}/worklog")
    public ResponseEntity<com.example.demo.entity.Worklog> logWork(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        Double hours = Double.valueOf(payload.get("hoursSpent").toString());
        String userEmail = payload.get("userEmail") != null ? payload.get("userEmail").toString() : "user@company.com";
        String userName = payload.get("userName") != null ? payload.get("userName").toString() : "Team Member";
        String description = payload.get("workDescription") != null ? payload.get("workDescription").toString() : "";

        com.example.demo.entity.Worklog logEntry = taskService.logWork(id, hours, userEmail, userName, description);
        return ResponseEntity.ok(logEntry);
    }

    @GetMapping("/{id}/worklog")
    public ResponseEntity<List<com.example.demo.entity.Worklog>> getTaskWorklogs(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskWorklogs(id));
    }
}