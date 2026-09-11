package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Company;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Project;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.ProjectService;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    private final ProjectService projectService;
    private final SecurityUtil securityUtil;
    private final CompanyRepository companyRepository;
    private final NotificationRepository notificationRepository;

    public ProjectController(ProjectService projectService,
                             SecurityUtil securityUtil,
                             CompanyRepository companyRepository,
                             NotificationRepository notificationRepository) {

        this.projectService = projectService;
        this.securityUtil = securityUtil;
        this.companyRepository = companyRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Project> getAllProjects(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        if (activeCompanyId != null) {
            return projectService.getProjectsByCompany(activeCompanyId);
        }
        return projectService.getAllProjects();
    }

    @GetMapping("/page")
    public org.springframework.http.ResponseEntity<com.example.demo.dto.PagedResponseDTO<Project>> getProjectsPaged(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Project> result = projectService.getProjectsByCompany(activeCompanyId, search, pageable);
        return org.springframework.http.ResponseEntity.ok(com.example.demo.dto.PagedResponseDTO.from(result));
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Integer id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public Project createProject(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Project project) {

        Integer activeCompanyId = securityUtil.extractCompanyId(
                authHeader,
                project.getCompany() != null ? project.getCompany().getId() : null
        );

        if (activeCompanyId != null) {
            Company company = companyRepository.findById(activeCompanyId).orElse(null);
            project.setCompany(company);
        }

        Project saved = projectService.saveProject(project);

        if (saved.getCompany() != null) {
            String leader = saved.getLeader() != null ? saved.getLeader() : "Team Leader";
            createNotification(saved.getCompany().getId(), "New Project Created: " + saved.getProjectName(),
                    "Project '" + saved.getProjectName() + "' created and assigned to Leader " + leader + ".",
                    "PROJECT", null, "ALL");
        }

        return saved;
    }

    @PutMapping("/{id}")
    public Project updateProject(
            @PathVariable Integer id,
            @RequestBody Project project) {

        Project updated = projectService.updateProject(id, project);

        if (updated.getCompany() != null) {
            createNotification(updated.getCompany().getId(), "Project Updated: " + updated.getProjectName(),
                    "Project '" + updated.getProjectName() + "' details or status updated.",
                    "PROJECT", null, "ALL");
        }

        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Integer id) {
        projectService.deleteProject(id);
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
}