package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.TaskRepository;

@RestController
@RequestMapping("/project")
public class ProjectAnalyticsController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/{id}/progress")
    public Map<String, Object> getProjectProgress(
            @PathVariable int id) {

        long total =
                taskRepository.countByProjectId(id);

        long completed =
                taskRepository.countByProjectIdAndStatus(
                        id,
                        "COMPLETED");

        double percentage = 0;

        if (total > 0) {
            percentage =
                    ((double) completed / total) * 100;
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put("projectId", id);
        data.put("totalTasks", total);
        data.put("completedTasks", completed);
        data.put("completionPercentage",
                 percentage);

        return data;
    }
}