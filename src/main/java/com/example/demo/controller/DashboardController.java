package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

@RestController
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/dashboard")
    public Map<String, Long> getDashboard() {

        Map<String, Long> data = new HashMap<>();

        data.put("totalUsers", userRepository.count());

        data.put("totalProjects", projectRepository.count());

        data.put("totalTasks", taskRepository.count());

        data.put("completedTasks",
                taskRepository.countByStatus("COMPLETED"));

        data.put("pendingTasks",
                taskRepository.countByStatus("PENDING"));

        data.put("inProgressTasks",
                taskRepository.countByStatus("IN_PROGRESS"));

        return data;
    }
}