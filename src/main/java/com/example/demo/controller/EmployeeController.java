package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.TaskRepository;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/{name}")
    public Map<String, Object> getEmployeePerformance(
            @PathVariable String name) {

        Map<String, Object> data = new HashMap<>();

        data.put("employee", name);

        data.put("totalTasks",
                taskRepository.countByAssignedTo(name));

        data.put("completedTasks",
                taskRepository.countByAssignedToAndStatus(
                        name,
                        "COMPLETED"));

        data.put("pendingTasks",
                taskRepository.countByAssignedToAndStatus(
                        name,
                        "PENDING"));

        data.put("inProgressTasks",
                taskRepository.countByAssignedToAndStatus(
                        name,
                        "IN_PROGRESS"));

        return data;
    }
}