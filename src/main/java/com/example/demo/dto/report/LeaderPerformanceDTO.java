package com.example.demo.dto.report;

public class LeaderPerformanceDTO {

    private Integer leaderId;
    private String leaderName;
    private String email;
    private String department;
    private String assignedProject;
    private int teamEmployeeCount;
    private int totalTeamTasks;
    private int completedTeamTasks;
    private int inProgressTeamTasks;
    private double teamCompletionRate;
    private String status;

    public LeaderPerformanceDTO() {
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(String assignedProject) {
        this.assignedProject = assignedProject;
    }

    public int getTeamEmployeeCount() {
        return teamEmployeeCount;
    }

    public void setTeamEmployeeCount(int teamEmployeeCount) {
        this.teamEmployeeCount = teamEmployeeCount;
    }

    public int getTotalTeamTasks() {
        return totalTeamTasks;
    }

    public void setTotalTeamTasks(int totalTeamTasks) {
        this.totalTeamTasks = totalTeamTasks;
    }

    public int getCompletedTeamTasks() {
        return completedTeamTasks;
    }

    public void setCompletedTeamTasks(int completedTeamTasks) {
        this.completedTeamTasks = completedTeamTasks;
    }

    public int getInProgressTeamTasks() {
        return inProgressTeamTasks;
    }

    public void setInProgressTeamTasks(int inProgressTeamTasks) {
        this.inProgressTeamTasks = inProgressTeamTasks;
    }

    public double getTeamCompletionRate() {
        return teamCompletionRate;
    }

    public void setTeamCompletionRate(double teamCompletionRate) {
        this.teamCompletionRate = teamCompletionRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
