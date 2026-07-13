package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAll();
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return service.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Integer id,
            @RequestBody Task task) {

        return service.update(id, task);

    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Integer id) {

        service.delete(id);

    }

}