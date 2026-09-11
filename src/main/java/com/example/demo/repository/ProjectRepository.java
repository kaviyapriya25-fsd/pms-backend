package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByCompany_Id(Integer companyId);

    Page<Project> findByCompany_Id(Integer companyId, Pageable pageable);

    List<Project> findByProjectNameContainingIgnoreCase(String projectName);

    List<Project> findByStatus(String status);

    List<Project> findByLeader(String leader);

    @Query("SELECT p FROM Project p WHERE p.company.id = :companyId AND " +
           "(LOWER(p.projectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.leader) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.status) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Project> searchProjects(@Param("companyId") Integer companyId, @Param("keyword") String keyword, Pageable pageable);
}