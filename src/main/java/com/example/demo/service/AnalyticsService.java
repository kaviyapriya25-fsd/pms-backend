package com.example.demo.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public AnalyticsService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Map<String, Object> getAiRiskPrediction(Integer companyId) {
        List<Task> tasks = companyId != null ? taskRepository.findByCompany_Id(companyId) : taskRepository.findAll();
        List<Project> projects = companyId != null ? projectRepository.findByCompany_Id(companyId) : projectRepository.findAll();

        LocalDate today = LocalDate.now();

        // 1. High Risk & Overdue Tasks
        List<Task> overdueTasks = tasks.stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getDueDate() != null && t.getDueDate().isBefore(today))
                .collect(Collectors.toList());

        List<Task> nearDeadlineTasks = tasks.stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getDueDate() != null &&
                        !t.getDueDate().isBefore(today) && t.getDueDate().isBefore(today.plusDays(3)))
                .collect(Collectors.toList());

        // 2. Team Workload Bottleneck Analysis
        Map<String, Long> employeeActiveTaskCount = tasks.stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getEmployee() != null && !t.getEmployee().isBlank())
                .collect(Collectors.groupingBy(Task::getEmployee, Collectors.counting()));

        List<Map<String, Object>> overloadedMembers = new ArrayList<>();
        employeeActiveTaskCount.forEach((member, count) -> {
            if (count >= 3) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", member);
                map.put("activeTasks", count);
                map.put("riskLevel", count >= 5 ? "HIGH" : "MEDIUM");
                overloadedMembers.add(map);
            }
        });

        // 3. AI Predictive Insights & Recommendations
        List<String> recommendations = new ArrayList<>();
        if (!overdueTasks.isEmpty()) {
            recommendations.add(overdueTasks.size() + " task(s) are currently OVERDUE. Immediate reassignment recommended.");
        }
        if (!nearDeadlineTasks.isEmpty()) {
            recommendations.add(nearDeadlineTasks.size() + " task(s) are due within 72 hours. Prioritize review.");
        }
        if (!overloadedMembers.isEmpty()) {
            recommendations.add(overloadedMembers.size() + " team member(s) carry 3+ active tasks. Rebalance workload to avoid burnout.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("All projects & sprints are operating within safe velocity thresholds.");
        }

        // 4. Overall Health Score (0 - 100%)
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
        double healthScore = totalTasks > 0 ? Math.round(((double) completedTasks / totalTasks) * 100) : 100.0;

        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", totalTasks);
        response.put("completedTasks", completedTasks);
        response.put("overdueCount", overdueTasks.size());
        response.put("nearDeadlineCount", nearDeadlineTasks.size());
        response.put("healthScore", healthScore);
        response.put("overloadedMembers", overloadedMembers);
        response.put("aiRecommendations", recommendations);

        return response;
    }
}
