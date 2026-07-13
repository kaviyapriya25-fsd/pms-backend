package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.TeamLeader;
import com.example.demo.service.TeamLeaderService;

@RestController
@RequestMapping("/teamleaders")
@CrossOrigin(origins = "http://localhost:5173")
public class TeamLeaderController {

    private final TeamLeaderService service;

    public TeamLeaderController(TeamLeaderService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamLeader> getAll() {
        return service.getAll();
    }

    @PostMapping
    public TeamLeader add(@RequestBody TeamLeader leader) {
        return service.save(leader);
    }

    @PutMapping("/{id}")
    public TeamLeader update(
            @PathVariable Integer id,
            @RequestBody TeamLeader leader) {

        return service.update(id, leader);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

}