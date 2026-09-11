package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Company;
import com.example.demo.repository.CompanyRepository;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company getCompanyProfile(Integer companyId) {
        if (companyId == null) {
            throw new RuntimeException("Company ID is required");
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company Not Found"));
    }

    public Company updateCompanyProfile(Integer companyId, Company updatedCompany) {
        Company existing = getCompanyProfile(companyId);

        if (updatedCompany.getCompanyName() != null && !updatedCompany.getCompanyName().isBlank()) {
            existing.setCompanyName(updatedCompany.getCompanyName());
        }
        if (updatedCompany.getCompanyEmail() != null && !updatedCompany.getCompanyEmail().isBlank()) {
            existing.setCompanyEmail(updatedCompany.getCompanyEmail());
        }
        if (updatedCompany.getPhone() != null) {
            existing.setPhone(updatedCompany.getPhone());
        }
        if (updatedCompany.getWebsite() != null) {
            existing.setWebsite(updatedCompany.getWebsite());
        }
        if (updatedCompany.getIndustry() != null) {
            existing.setIndustry(updatedCompany.getIndustry());
        }
        if (updatedCompany.getCompanySize() != null) {
            existing.setCompanySize(updatedCompany.getCompanySize());
        }
        if (updatedCompany.getAddress() != null) {
            existing.setAddress(updatedCompany.getAddress());
        }
        if (updatedCompany.getLogo() != null) {
            existing.setLogo(updatedCompany.getLogo());
        }

        return companyRepository.save(existing);
    }

    public Company updateCompanyLogo(Integer companyId, String logoUrl) {
        Company existing = getCompanyProfile(companyId);
        existing.setLogo(logoUrl);
        return companyRepository.save(existing);
    }
}
