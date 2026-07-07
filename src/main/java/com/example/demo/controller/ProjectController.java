package com.example.demo.controller;


import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.demo.entity.Project;
import com.example.demo.repository.ProjectRepository;



@RestController
@RequestMapping("/projects")
public class ProjectController {



    private final ProjectRepository projectRepository;



    public ProjectController(ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;

    }




    @GetMapping
    public List<Project> getAllProjects() {

        return projectRepository.findAll();

    }




    @PostMapping
    public Project addProject(@RequestBody Project project) {

        return projectRepository.save(project);

    }



}