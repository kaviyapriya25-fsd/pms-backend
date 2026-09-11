package com.example.demo.dto.report;

import java.util.List;

public class CompanyReportSummaryDTO {

    private Integer companyId;
    private String companyName;
    private int totalProjects;
    private int totalTeamLeaders;
    private int totalEmployees;
    private int totalTasks;
    private double overallProjectCompletionRate;

    private List<EmployeePerformanceDTO> employeePerformanceList;
    private List<LeaderPerformanceDTO> leaderPerformanceList;
    private List<ProjectProgressDTO> projectProgressList;
    private TaskCompletionDTO taskCompletionReport;

    public CompanyReportSummaryDTO() {
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getTotalTeamLeaders() {
        return totalTeamLeaders;
    }

    public void setTotalTeamLeaders(int totalTeamLeaders) {
        this.totalTeamLeaders = totalTeamLeaders;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public double getOverallProjectCompletionRate() {
        return overallProjectCompletionRate;
    }

    public void setOverallProjectCompletionRate(double overallProjectCompletionRate) {
        this.overallProjectCompletionRate = overallProjectCompletionRate;
    }

    public List<EmployeePerformanceDTO> getEmployeePerformanceList() {
        return employeePerformanceList;
    }

    public void setEmployeePerformanceList(List<EmployeePerformanceDTO> employeePerformanceList) {
        this.employeePerformanceList = employeePerformanceList;
    }

    public List<LeaderPerformanceDTO> getLeaderPerformanceList() {
        return leaderPerformanceList;
    }

    public void setLeaderPerformanceList(List<LeaderPerformanceDTO> leaderPerformanceList) {
        this.leaderPerformanceList = leaderPerformanceList;
    }

    public List<ProjectProgressDTO> getProjectProgressList() {
        return projectProgressList;
    }

    public void setProjectProgressList(List<ProjectProgressDTO> projectProgressList) {
        this.projectProgressList = projectProgressList;
    }

    public TaskCompletionDTO getTaskCompletionReport() {
        return taskCompletionReport;
    }

    public void setTaskCompletionReport(TaskCompletionDTO taskCompletionReport) {
        this.taskCompletionReport = taskCompletionReport;
    }
}
