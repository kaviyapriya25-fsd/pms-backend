package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RegisterCompanyRequest;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Company;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CompanyRepository;

@Service
public class AuthService {

    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;

    public AuthService(
            CompanyRepository companyRepository,
            AdminRepository adminRepository) {

        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
    }

    public String registerCompany(RegisterCompanyRequest request) {

        if (companyRepository.existsByCompanyEmail(request.getCompanyEmail())) {
            return "Company email already exists";
        }

        if (adminRepository.existsByAdminEmail(request.getAdminEmail())) {
            return "Admin email already exists";
        }

        Company company = new Company();

        company.setCompanyName(request.getCompanyName());
        company.setCompanyEmail(request.getCompanyEmail());
        company.setPhone(request.getPhone());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setCompanySize(request.getCompanySize());
        company.setAddress(request.getAddress());

        company = companyRepository.save(company);

        Admin admin = new Admin();

        admin.setAdminName(request.getAdminName());
        admin.setAdminEmail(request.getAdminEmail());

        // NOTE:
        // For now we're storing plain text.
        // Later we'll use BCryptPasswordEncoder.
        admin.setPassword(request.getPassword());

        admin.setCompany(company);

        adminRepository.save(admin);

        return "Company Registered Successfully";
    }

}