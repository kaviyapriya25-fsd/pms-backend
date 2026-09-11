package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByName(String name);

    boolean existsByEmail(String email);

    List<Employee> findByCompany_Id(Integer companyId);

    Page<Employee> findByCompany_Id(Integer companyId, Pageable pageable);

    List<Employee> findByTeamLeader(String teamLeader);

}