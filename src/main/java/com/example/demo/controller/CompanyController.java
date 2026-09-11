package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Company;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.CompanyService;

@RestController
@RequestMapping("/company")
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyController {

    private final CompanyService companyService;
    private final SecurityUtil securityUtil;

    public CompanyController(CompanyService companyService, SecurityUtil securityUtil) {
        this.companyService = companyService;
        this.securityUtil = securityUtil;
    }

    // 1. GET Company Profile (reads companyId strictly from JWT)
    @GetMapping("/profile")
    public ResponseEntity<Company> getProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        Company company = companyService.getCompanyProfile(activeCompanyId);
        return ResponseEntity.ok(company);
    }

    // 2. PUT Update Company Profile (reads companyId strictly from JWT)
    @PutMapping("/profile")
    public ResponseEntity<Company> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestBody Company updatedCompany) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        Company updated = companyService.updateCompanyProfile(activeCompanyId, updatedCompany);
        return ResponseEntity.ok(updated);
    }

    // 3. POST Update Company Logo
    @PostMapping("/logo")
    public ResponseEntity<Company> updateLogo(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestBody String logoUrl) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        Company updated = companyService.updateCompanyLogo(activeCompanyId, logoUrl);
        return ResponseEntity.ok(updated);
    }
}
