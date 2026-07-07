package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    long countByStatus(String status);

    long countByAssignedTo(String assignedTo);

    long countByAssignedToAndStatus(String assignedTo,
                                    String status);

    long countByProjectId(int projectId);

    long countByProjectIdAndStatus(int projectId,
                                   String status);
}