package com.example.demo.controller;


import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;



@RestController
@RequestMapping("/tasks")
public class TaskController {



    private final TaskRepository taskRepository;



    public TaskController(TaskRepository taskRepository) {

        this.taskRepository = taskRepository;

    }




    @GetMapping
    public List<Task> getAllTasks() {

        return taskRepository.findAll();

    }





    @PostMapping
    public Task addTask(@RequestBody Task task) {

        return taskRepository.save(task);

    }

    @PutMapping("/{id}/status")
    public Task updateStatus(@PathVariable int id,
                             @RequestParam String status) {

        Task task = taskRepository.findById(id).orElse(null);

        if (task != null) {
            task.setStatus(status);
            return taskRepository.save(task);
        }

        return null;
    }

}