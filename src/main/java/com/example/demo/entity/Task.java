package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String taskName;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private String status;

    private LocalDate startDate;

    private LocalDate dueDate;

    // Assigned Project
    @Column(nullable = false)
    private String project;

    // Assigned Team Leader Email or Name
    @Column(nullable = false)
    private String teamLeader;

    // Assigned Employee Email or Name
    @Column(nullable = false)
    private String employee;

    // Upload Work / Deliverables Submission (URL or Notes)
    @Column(length = 2000)
    private String workSubmission;

    // Task Discussion Comments
    @Column(length = 4000)
    private String comments;

    // Agile Sprint & Time Estimation
    private String sprintName = "Sprint 1";

    private Double estimatedHours = 8.0;

    private Double loggedHours = 0.0;

    private Integer storyPoints = 3;

    // Granular Technical Task Progress (Jira Sub-task style)
    private Boolean backendCompleted = false;

    private Boolean frontendCompleted = false;

    private Boolean integrationCompleted = false;

    private Integer progressPercentage = 0;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void onCreateOrUpdate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) status = "TODO";
        if (priority == null) priority = "MEDIUM";
        if (startDate == null) startDate = LocalDate.now();
        if (sprintName == null) sprintName = "Sprint 1";
        if (estimatedHours == null) estimatedHours = 8.0;
        if (loggedHours == null) loggedHours = 0.0;
        if (storyPoints == null) storyPoints = 3;

        if (backendCompleted == null) backendCompleted = false;
        if (frontendCompleted == null) frontendCompleted = false;
        if (integrationCompleted == null) integrationCompleted = false;

        // Auto calculate progress percentage
        int count = 0;
        if (Boolean.TRUE.equals(backendCompleted)) count++;
        if (Boolean.TRUE.equals(frontendCompleted)) count++;
        if (Boolean.TRUE.equals(integrationCompleted)) count++;

        this.progressPercentage = (int) Math.round((count / 3.0) * 100);

        // Auto sync overall status with technical completion
        if (count == 3) {
            this.status = "COMPLETED";
        } else if (count > 0 && "TODO".equalsIgnoreCase(this.status)) {
            this.status = "IN_PROGRESS";
        }
    }

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getWorkSubmission() {
        return workSubmission;
    }

    public void setWorkSubmission(String workSubmission) {
        this.workSubmission = workSubmission;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getLoggedHours() {
        return loggedHours;
    }

    public void setLoggedHours(Double loggedHours) {
        this.loggedHours = loggedHours;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getBackendCompleted() {
        return backendCompleted != null && backendCompleted;
    }

    public void setBackendCompleted(Boolean backendCompleted) {
        this.backendCompleted = backendCompleted;
    }

    public Boolean getFrontendCompleted() {
        return frontendCompleted != null && frontendCompleted;
    }

    public void setFrontendCompleted(Boolean frontendCompleted) {
        this.frontendCompleted = frontendCompleted;
    }

    public Boolean getIntegrationCompleted() {
        return integrationCompleted != null && integrationCompleted;
    }

    public void setIntegrationCompleted(Boolean integrationCompleted) {
        this.integrationCompleted = integrationCompleted;
    }

    public Integer getProgressPercentage() {
        if (progressPercentage == null) {
            int count = 0;
            if (Boolean.TRUE.equals(backendCompleted)) count++;
            if (Boolean.TRUE.equals(frontendCompleted)) count++;
            if (Boolean.TRUE.equals(integrationCompleted)) count++;
            progressPercentage = (int) Math.round((count / 3.0) * 100);
        }
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}