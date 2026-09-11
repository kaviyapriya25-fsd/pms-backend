package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    boolean existsByCompanyEmail(String companyEmail);

    Optional<Company> findByCompanyEmail(String companyEmail);

    @Modifying
    @Query("DELETE FROM Company c WHERE c.companyEmail IS NULL OR c.companyEmail = ''")
    int deleteByCompanyEmailIsNullOrCompanyEmailIsEmpty();

}