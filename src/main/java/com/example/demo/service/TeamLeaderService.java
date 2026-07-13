package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.TeamLeader;
import com.example.demo.repository.TeamLeaderRepository;

@Service
public class TeamLeaderService {

    private final TeamLeaderRepository repository;

    public TeamLeaderService(TeamLeaderRepository repository) {
        this.repository = repository;
    }

    public List<TeamLeader> getAll() {
        return repository.findAll();
    }

    public TeamLeader save(TeamLeader leader) {
        return repository.save(leader);
    }

    public TeamLeader update(Integer id, TeamLeader leader) {

        TeamLeader existing = repository.findById(id).orElseThrow();

        existing.setName(leader.getName());
        existing.setEmail(leader.getEmail());
        existing.setPhone(leader.getPhone());
        existing.setDepartment(leader.getDepartment());
        existing.setProject(leader.getProject());
        existing.setStatus(leader.getStatus());

        return repository.save(existing);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

}