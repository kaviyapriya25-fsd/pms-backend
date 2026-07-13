package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {

        Map<String, Object> data = new HashMap<>();

        data.put("employees", 25);
        data.put("teamLeaders", 5);
        data.put("projects", 8);
        data.put("tasks", 96);
        data.put("completedTasks", 74);
        data.put("pendingTasks", 22);

        return data;
    }

}