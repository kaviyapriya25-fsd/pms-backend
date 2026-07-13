package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Project;
import com.example.demo.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // Get All Projects
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Get Project By ID
    public Project getProjectById(int id) {
        return projectRepository.findById(id).orElse(null);
    }

    // Save Project
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    // Update Project
    public Project updateProject(int id, Project project) {

        Project existing = projectRepository.findById(id).orElse(null);

        if (existing != null) {

            existing.setProjectName(project.getProjectName());
            existing.setDescription(project.getDescription());
            existing.setLeader(project.getLeader());
            existing.setStartDate(project.getStartDate());
            existing.setEndDate(project.getEndDate());
            existing.setStatus(project.getStatus());

            return projectRepository.save(existing);
        }

        return null;
    }

    // Delete Project
    public void deleteProject(int id) {
        projectRepository.deleteById(id);
    }
}