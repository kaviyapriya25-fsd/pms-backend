package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByCompany_Id(Integer companyId);

    Page<Task> findByCompany_Id(Integer companyId, Pageable pageable);

    List<Task> findByEmployee(String employee);

    List<Task> findByTeamLeader(String teamLeader);

    List<Task> findByProject(String project);

    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    @Query("SELECT t FROM Task t WHERE t.company.id = :companyId AND " +
           "(LOWER(t.taskName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.priority) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> searchTasks(@Param("companyId") Integer companyId, @Param("keyword") String keyword, Pageable pageable);
}