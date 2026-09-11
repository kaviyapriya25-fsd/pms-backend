package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {

    private long employees;
    private long teamLeaders;
    private long projects;
    private long activeProjects;
    private long inProgressProjects;
    private long completedProjects;
    private long tasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;

    private List<ProjectDTO> recentProjects = new ArrayList<>();
    private List<ActivityDTO> recentActivities = new ArrayList<>();
    private List<NotificationDTO> recentNotifications = new ArrayList<>();

    public DashboardResponse() {
    }

    public long getEmployees() {
        return employees;
    }

    public void setEmployees(long employees) {
        this.employees = employees;
    }

    public long getTeamLeaders() {
        return teamLeaders;
    }

    public void setTeamLeaders(long teamLeaders) {
        this.teamLeaders = teamLeaders;
    }

    public long getProjects() {
        return projects;
    }

    public void setProjects(long projects) {
        this.projects = projects;
    }

    public long getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(long activeProjects) {
        this.activeProjects = activeProjects;
    }

    public long getInProgressProjects() {
        return inProgressProjects;
    }

    public void setInProgressProjects(long inProgressProjects) {
        this.inProgressProjects = inProgressProjects;
    }

    public long getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(long completedProjects) {
        this.completedProjects = completedProjects;
    }

    public long getTasks() {
        return tasks;
    }

    public void setTasks(long tasks) {
        this.tasks = tasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public List<ProjectDTO> getRecentProjects() {
        return recentProjects;
    }

    public void setRecentProjects(List<ProjectDTO> recentProjects) {
        this.recentProjects = recentProjects != null ? recentProjects : new ArrayList<>();
    }

    public List<ActivityDTO> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityDTO> recentActivities) {
        this.recentActivities = recentActivities != null ? recentActivities : new ArrayList<>();
    }

    public List<NotificationDTO> getRecentNotifications() {
        return recentNotifications;
    }

    public void setRecentNotifications(List<NotificationDTO> recentNotifications) {
        this.recentNotifications = recentNotifications != null ? recentNotifications : new ArrayList<>();
    }
}