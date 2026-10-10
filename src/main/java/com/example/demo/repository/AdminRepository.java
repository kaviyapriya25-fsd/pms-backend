
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByAdminEmail(String adminEmail);

    Admin findByAdminEmail(String adminEmail);

    @Modifying
    @Query("DELETE FROM Admin a WHERE a.adminEmail IS NULL OR a.adminEmail = ''")
    int deleteByAdminEmailIsNullOrAdminEmailIsEmpty();

    @Modifying
    @Query("DELETE FROM Admin a WHERE a.company.id = :companyId")
    int deleteByCompanyId(int companyId);

}