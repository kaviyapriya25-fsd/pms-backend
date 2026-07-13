package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository){
        this.repository = repository;
    }

    @GetMapping
    public List<Notification> getAll(){
        return repository.findAll();
    }

    @PostMapping
    public Notification add(@RequestBody Notification notification){
        return repository.save(notification);
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable int id,@RequestBody Notification notification){

        notification.setId(id);
        return repository.save(notification);

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){
        repository.deleteById(id);
    }

}