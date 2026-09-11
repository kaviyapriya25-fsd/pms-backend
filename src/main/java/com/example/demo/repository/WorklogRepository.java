package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Worklog;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Integer> {
    List<Worklog> findByTaskIdOrderByLoggedAtDesc(Integer taskId);
    List<Worklog> findByUserEmail(String userEmail);
}
