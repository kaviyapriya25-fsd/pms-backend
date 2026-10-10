package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ActivityDTO;
import com.example.demo.dto.DashboardResponse;
import com.example.demo.dto.LeaderDashboardResponse;
import com.example.demo.dto.NotificationDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.TeamLeader;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.TeamLeaderRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

    public DashboardService(
            EmployeeRepository employeeRepository,
            TeamLeaderRepository teamLeaderRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            NotificationRepository notificationRepository) {

        this.employeeRepository = employeeRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
    }

    public DashboardResponse getDashboard(Integer companyId) {

        DashboardResponse response = new DashboardResponse();

        if (companyId == null) {
            return response;
        }

        var employees = employeeRepository.findByCompany_Id(companyId);
        response.setEmployees(employees != null ? employees.size() : 0);

        var leaders = teamLeaderRepository.findByCompany_Id(companyId);
        response.setTeamLeaders(leaders != null ? leaders.size() : 0);

        var projects = projectRepository.findByCompany_Id(companyId);
        if (projects != null) {
            response.setProjects(projects.size());
            response.setActiveProjects(projects.stream()
                    .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                    .count());
            response.setInProgressProjects(projects.stream()
                    .filter(p -> "IN_PROGRESS".equalsIgnoreCase(p.getStatus()))
                    .count());
            response.setCompletedProjects(projects.stream()
                    .filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus()))
                    .count());

            List<ProjectDTO> recentProjectDTOs = projects.stream()
                    .skip(Math.max(0, projects.size() - 5))
                    .map(this::mapToProjectDTO)
                    .collect(Collectors.toList());
            
            java.util.Collections.reverse(recentProjectDTOs);
            response.setRecentProjects(recentProjectDTOs);
        }

        var tasks = taskRepository.findByCompany_Id(companyId);
        if (tasks != null) {
            response.setTasks(tasks.size());
            response.setPendingTasks(tasks.stream()
                    .filter(t -> "TODO".equalsIgnoreCase(t.getStatus()))
                    .count());
            response.setInProgressTasks(tasks.stream()
                    .filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                    .count());
            response.setCompletedTasks(tasks.stream()
                    .filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()))
                    .count());
        }

        List<ActivityDTO> activities = new ArrayList<>();
        if (projects != null && !projects.isEmpty()) {
            int count = 1;
            for (int i = projects.size() - 1; i >= Math.max(0, projects.size() - 3); i--) {
                Project p = projects.get(i);
                activities.add(new ActivityDTO(
                        count++,
                        "Project \"" + p.getProjectName() + "\" created",
                        p.getStartDate() != null ? p.getStartDate().toString() : "Recently",
                        "PROJECT"
                ));
            }
        }

        if (tasks != null && !tasks.isEmpty()) {
            int count = activities.size() + 1;
            for (int i = tasks.size() - 1; i >= Math.max(0, tasks.size() - 3); i--) {
                Task t = tasks.get(i);
                activities.add(new ActivityDTO(
                        count++,
                        "Task \"" + t.getTaskName() + "\" assigned to " + (t.getEmployee() != null ? t.getEmployee() : "team"),
                        t.getStartDate() != null ? t.getStartDate().toString() : "Recently",
                        "TASK"
                ));
            }
        }
        response.setRecentActivities(activities);

        var notifications = notificationRepository.findByCompanyId(companyId);
        if (notifications != null) {
            List<NotificationDTO> notifDTOs = notifications.stream()
                    .skip(Math.max(0, notifications.size() - 5))
                    .map(this::mapToNotificationDTO)
                    .collect(Collectors.toList());
            java.util.Collections.reverse(notifDTOs);
            response.setRecentNotifications(notifDTOs);
        }

        return response;
    }

    public LeaderDashboardResponse getLeaderDashboard(Integer companyId, String leaderEmail, String leaderName) {

        LeaderDashboardResponse response = new LeaderDashboardResponse();
        if (companyId == null) {
            return response;
        }

        // 1. Fetch team leader record
        TeamLeader leader = teamLeaderRepository.findByCompany_Id(companyId).stream()
                .filter(l -> (leaderEmail != null && leaderEmail.equalsIgnoreCase(l.getEmail())) ||
                             (leaderName != null && leaderName.equalsIgnoreCase(l.getName())))
                .findFirst()
                .orElse(null);

        // Fetch ALL projects assigned to this leader
        List<Project> allCompanyProjects = projectRepository.findByCompany_Id(companyId);
        List<Project> myProjects = allCompanyProjects.stream()
                .filter(p -> (leaderName != null && leaderName.equalsIgnoreCase(p.getLeader())) ||
                             (leaderEmail != null && leaderEmail.equalsIgnoreCase(p.getLeader())) ||
                             (leader != null && leader.getProject() != null && leader.getProject().equalsIgnoreCase(p.getProjectName())))
                .collect(Collectors.toList());

        List<LeaderDashboardResponse.ProjectSummary> projectSummaries = myProjects.stream()
                .map(p -> new LeaderDashboardResponse.ProjectSummary(
                        p.getId(),
                        p.getProjectName(),
                        p.getDescription(),
                        p.getStatus(),
                        p.getPriority(),
                        p.getStartDate() != null ? p.getStartDate().toString() : "",
                        p.getEndDate() != null ? p.getEndDate().toString() : ""
                ))
                .collect(Collectors.toList());
        response.setAssignedProjects(projectSummaries);

        // Primary / default project for legacy single-project fields:
        Project primaryProject = !myProjects.isEmpty() ? myProjects.get(0) : null;
        if (primaryProject != null) {
            response.setAssignedProjectName(primaryProject.getProjectName());
            response.setAssignedProjectStatus(primaryProject.getStatus());
            response.setAssignedProjectStartDate(primaryProject.getStartDate() != null ? primaryProject.getStartDate().toString() : "");
            response.setAssignedProjectEndDate(primaryProject.getEndDate() != null ? primaryProject.getEndDate().toString() : "");
            response.setAssignedProjectDescription(primaryProject.getDescription());
            response.setAssignedProjectPriority(primaryProject.getPriority());
        } else {
            response.setAssignedProjectName("Unassigned");
            response.setAssignedProjectStatus("PENDING");
        }

        // 2. Fetch Employees under this Team Leader
        List<Employee> companyEmployees = employeeRepository.findByCompany_Id(companyId);
        List<Employee> myTeam = companyEmployees.stream()
                .filter(e -> (leaderName != null && leaderName.equalsIgnoreCase(e.getTeamLeader())) ||
                             (leaderEmail != null && leaderEmail.equalsIgnoreCase(e.getTeamLeader())))
                .collect(Collectors.toList());

        response.setTeamMembersCount(myTeam.size());

        // 3. Fetch Tasks created by or under this Team Leader / Projects
        java.util.Set<String> myProjectNames = myProjects.stream()
                .map(p -> p.getProjectName() != null ? p.getProjectName().trim().toLowerCase() : "")
                .collect(Collectors.toSet());

        List<Task> companyTasks = taskRepository.findByCompany_Id(companyId);
        List<Task> myTasks = companyTasks.stream()
                .filter(t -> (leaderName != null && leaderName.equalsIgnoreCase(t.getTeamLeader())) ||
                             (leaderEmail != null && leaderEmail.equalsIgnoreCase(t.getTeamLeader())) ||
                             (t.getProject() != null && myProjectNames.contains(t.getProject().trim().toLowerCase())))
                .collect(Collectors.toList());

        int totalTasks = myTasks.size();
        int todoTasks = (int) myTasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).count();
        int inProgressTasks = (int) myTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
        int completedTasks = (int) myTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
        int urgentTasks = (int) myTasks.stream().filter(t -> "URGENT".equalsIgnoreCase(t.getPriority()) || "HIGH".equalsIgnoreCase(t.getPriority())).count();
        int completionPercentage = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;

        response.setTotalTasks(totalTasks);
        response.setTodoTasks(todoTasks);
        response.setInProgressTasks(inProgressTasks);
        response.setCompletedTasks(completedTasks);
        response.setUrgentTasks(urgentTasks);
        response.setCompletionPercentage(completionPercentage);

        // 4. Employee Summaries with individual task counts
        List<LeaderDashboardResponse.EmployeeSummary> employeeSummaries = myTeam.stream().map(emp -> {
            long empTotalTasks = myTasks.stream().filter(t -> emp.getName().equalsIgnoreCase(t.getEmployee())).count();
            long empCompletedTasks = myTasks.stream().filter(t -> emp.getName().equalsIgnoreCase(t.getEmployee()) && "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            return new LeaderDashboardResponse.EmployeeSummary(
                    emp.getId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getPhone(),
                    emp.getDesignation(),
                    emp.getStatus(),
                    (int) empTotalTasks,
                    (int) empCompletedTasks
            );
        }).collect(Collectors.toList());

        response.setTeamMembers(employeeSummaries);

        // 5. Recent Activities for this Team Leader
        List<ActivityDTO> activities = new ArrayList<>();
        int count = 1;
        for (int i = myTasks.size() - 1; i >= Math.max(0, myTasks.size() - 5); i--) {
            Task t = myTasks.get(i);
            activities.add(new ActivityDTO(
                    count++,
                    "Task \"" + t.getTaskName() + "\" (" + t.getStatus() + ") assigned to " + (t.getEmployee() != null ? t.getEmployee() : "team"),
                    t.getStartDate() != null ? t.getStartDate().toString() : "Recently",
                    "TASK"
            ));
        }
        response.setRecentActivities(activities);

        // 6. Recent Notifications for this company
        List<Notification> notifications = notificationRepository.findByCompanyId(companyId);
        if (notifications != null) {
            List<NotificationDTO> notifDTOs = notifications.stream()
                    .skip(Math.max(0, notifications.size() - 5))
                    .map(this::mapToNotificationDTO)
                    .collect(Collectors.toList());
            java.util.Collections.reverse(notifDTOs);
            response.setNotifications(notifDTOs);
        }

        return response;
    }

    private ProjectDTO mapToProjectDTO(Project project) {
        if (project == null) return null;
        return new ProjectDTO(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getLeader(),
                project.getPriority(),
                project.getStatus(),
                project.getStartDate() != null ? project.getStartDate().toString() : "",
                project.getEndDate() != null ? project.getEndDate().toString() : ""
        );
    }

    private NotificationDTO mapToNotificationDTO(Notification n) {
        if (n == null) return null;
        return new NotificationDTO(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getTime(),
                n.isRead()
        );
    }

}