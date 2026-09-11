package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    public Notification createNotification(Integer companyId, String title, String message, String type, String recipientEmail, String recipientRole) {
        Notification notification = new Notification();
        notification.setCompanyId(companyId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : "INFO");
        notification.setRecipientEmail(recipientEmail);
        notification.setRecipientRole(recipientRole != null ? recipientRole : "ALL");
        notification.setRead(false);
        return repository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Integer companyId, String email, String role) {
        if (companyId == null) {
            return repository.findAll();
        }
        String safeEmail = (email != null) ? email.trim() : "";
        String safeRole = (role != null) ? role.trim() : "";
        
        if (!safeEmail.isEmpty() || !safeRole.isEmpty()) {
            return repository.findForUser(companyId, safeEmail, safeRole);
        }
        return repository.findByCompanyIdOrderByIdDesc(companyId);
    }

    public long getUnreadCountForUser(Integer companyId, String email, String role) {
        if (companyId == null) {
            return repository.countByIsRead(false);
        }
        String safeEmail = (email != null) ? email.trim() : "";
        String safeRole = (role != null) ? role.trim() : "";

        if (!safeEmail.isEmpty() || !safeRole.isEmpty()) {
            return repository.countUnreadForUser(companyId, safeEmail, safeRole);
        }
        return repository.countByCompanyIdAndIsRead(companyId, false);
    }

    public Notification markAsRead(int id) {
        return repository.findById(id).map(n -> {
            n.setRead(true);
            return repository.save(n);
        }).orElse(null);
    }

    @Transactional
    public void markAllAsRead(Integer companyId, String email, String role) {
        List<Notification> list = getNotificationsForUser(companyId, email, role);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
                repository.save(n);
            }
        }
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    // Helper triggers for system events
    public void notifyTaskAssigned(Task task) {
        if (task == null) return;
        try {
            String title = "Task Assigned: " + task.getTaskName();
            String message = "You have been assigned to task '" + task.getTaskName() + "'";
            if (task.getProject() != null && !task.getProject().isBlank()) {
                message += " in project '" + task.getProject() + "'";
            }
            if (task.getDueDate() != null) {
                message += " (Due: " + task.getDueDate() + ")";
            }
            message += ". Priority: " + (task.getPriority() != null ? task.getPriority() : "MEDIUM");

            Integer companyId = (task.getCompany() != null) ? task.getCompany().getId() : null;

            createNotification(
                    companyId,
                    title,
                    message,
                    "TASK",
                    task.getEmployee(),
                    "EMPLOYEE"
            );
        } catch (Exception e) {
            log.error("Failed to create task assignment notification: {}", e.getMessage());
        }
    }

    public void notifyProjectAssigned(Project project) {
        if (project == null) return;
        try {
            String title = "New Project Assigned: " + project.getProjectName();
            String message = "You have been assigned as leader for project '" + project.getProjectName() + "'.";
            if (project.getDescription() != null && !project.getDescription().isBlank()) {
                message += " " + project.getDescription();
            }

            Integer companyId = (project.getCompany() != null) ? project.getCompany().getId() : null;

            createNotification(
                    companyId,
                    title,
                    message,
                    "PROJECT",
                    project.getLeader(),
                    "LEADER"
            );
        } catch (Exception e) {
            log.error("Failed to create project assignment notification: {}", e.getMessage());
        }
    }

    public void notifyWorklogLogged(Task task, String userEmail, String userName, Double hoursSpent) {
        if (task == null) return;
        try {
            String displayName = (userName != null && !userName.isBlank()) ? userName : userEmail;
            String title = "Work Logged on Task: " + task.getTaskName();
            String message = displayName + " logged " + hoursSpent + " hours on task '" + task.getTaskName() + "'.";

            Integer companyId = (task.getCompany() != null) ? task.getCompany().getId() : null;

            // Broadcast to leaders/admins
            createNotification(
                    companyId,
                    title,
                    message,
                    "WORKLOG",
                    null,
                    "LEADER"
            );
        } catch (Exception e) {
            log.error("Failed to create worklog notification: {}", e.getMessage());
        }
    }
}
