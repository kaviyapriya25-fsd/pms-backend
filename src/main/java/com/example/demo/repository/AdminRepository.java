package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByAdminEmail(String adminEmail);

    Admin findByAdminEmail(String adminEmail);

}