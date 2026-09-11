package com.example.demo.dto;

public class ProjectDTO {

    private Integer id;
    private String projectName;
    private String description;
    private String leader;
    private String priority;
    private String status;
    private String startDate;
    private String endDate;

    public ProjectDTO() {
    }

    public ProjectDTO(Integer id, String projectName, String description, String leader,
                      String priority, String status, String startDate, String endDate) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.leader = leader;
        this.priority = priority;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
