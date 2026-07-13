package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAll() {
        return repository.findAll();
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public Task update(Integer id, Task task) {

        Task existing = repository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setAssignedTo(task.getAssignedTo());
        existing.setAssignedBy(task.getAssignedBy());
        existing.setProjectId(task.getProjectId());
        existing.setStatus(task.getStatus());

        return repository.save(existing);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

}