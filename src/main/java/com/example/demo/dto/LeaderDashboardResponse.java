package com.example.demo.dto;

import java.util.List;

public class LeaderDashboardResponse {

    private String assignedProjectName;
    private String assignedProjectStatus;
    private String assignedProjectStartDate;
    private String assignedProjectEndDate;
    private String assignedProjectDescription;
    private String assignedProjectPriority;

    private int teamMembersCount;
    private int totalTasks;
    private int todoTasks;
    private int inProgressTasks;
    private int completedTasks;
    private int urgentTasks;
    private int completionPercentage;

    private List<EmployeeSummary> teamMembers;
    private List<ActivityDTO> recentActivities;
    private List<NotificationDTO> notifications;

    public static class EmployeeSummary {
        private Integer id;
        private String name;
        private String email;
        private String phone;
        private String designation;
        private String status;
        private int totalTasks;
        private int completedTasks;

        public EmployeeSummary() {}

        public EmployeeSummary(Integer id, String name, String email, String phone, String designation, String status, int totalTasks, int completedTasks) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.designation = designation;
            this.status = status;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    }

    public LeaderDashboardResponse() {}

    public String getAssignedProjectName() { return assignedProjectName; }
    public void setAssignedProjectName(String assignedProjectName) { this.assignedProjectName = assignedProjectName; }

    public String getAssignedProjectStatus() { return assignedProjectStatus; }
    public void setAssignedProjectStatus(String assignedProjectStatus) { this.assignedProjectStatus = assignedProjectStatus; }

    public String getAssignedProjectStartDate() { return assignedProjectStartDate; }
    public void setAssignedProjectStartDate(String assignedProjectStartDate) { this.assignedProjectStartDate = assignedProjectStartDate; }

    public String getAssignedProjectEndDate() { return assignedProjectEndDate; }
    public void setAssignedProjectEndDate(String assignedProjectEndDate) { this.assignedProjectEndDate = assignedProjectEndDate; }

    public String getAssignedProjectDescription() { return assignedProjectDescription; }
    public void setAssignedProjectDescription(String assignedProjectDescription) { this.assignedProjectDescription = assignedProjectDescription; }

    public String getAssignedProjectPriority() { return assignedProjectPriority; }
    public void setAssignedProjectPriority(String assignedProjectPriority) { this.assignedProjectPriority = assignedProjectPriority; }

    public int getTeamMembersCount() { return teamMembersCount; }
    public void setTeamMembersCount(int teamMembersCount) { this.teamMembersCount = teamMembersCount; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getTodoTasks() { return todoTasks; }
    public void setTodoTasks(int todoTasks) { this.todoTasks = todoTasks; }

    public int getInProgressTasks() { return inProgressTasks; }
    public void setInProgressTasks(int inProgressTasks) { this.inProgressTasks = inProgressTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public int getUrgentTasks() { return urgentTasks; }
    public void setUrgentTasks(int urgentTasks) { this.urgentTasks = urgentTasks; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }

    public List<EmployeeSummary> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<EmployeeSummary> teamMembers) { this.teamMembers = teamMembers; }

    public List<ActivityDTO> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<ActivityDTO> recentActivities) { this.recentActivities = recentActivities; }

    public List<NotificationDTO> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDTO> notifications) { this.notifications = notifications; }
}
