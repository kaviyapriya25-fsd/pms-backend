package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TeamLeader;

public interface TeamLeaderRepository extends JpaRepository<TeamLeader, Integer> {

    Optional<TeamLeader> findByEmail(String email);

    Optional<TeamLeader> findByName(String name);

    boolean existsByEmail(String email);

    List<TeamLeader> findByCompany_Id(Integer companyId);

    Page<TeamLeader> findByCompany_Id(Integer companyId, Pageable pageable);

}