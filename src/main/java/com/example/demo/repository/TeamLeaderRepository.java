package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TeamLeader;

public interface TeamLeaderRepository extends JpaRepository<TeamLeader, Integer> {

}