package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Company;
import com.example.demo.entity.TeamLeader;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.TeamLeaderService;

@RestController
@RequestMapping("/teamleaders")
@CrossOrigin(origins = "http://localhost:5173")
public class TeamLeaderController {

    private final TeamLeaderService service;
    private final SecurityUtil securityUtil;
    private final CompanyRepository companyRepository;

    public TeamLeaderController(TeamLeaderService service,
                                SecurityUtil securityUtil,
                                CompanyRepository companyRepository) {

        this.service = service;
        this.securityUtil = securityUtil;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<TeamLeader> getAllTeamLeaders(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        return service.getAllTeamLeaders(activeCompanyId);
    }

    @PostMapping
    public TeamLeader addTeamLeader(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody TeamLeader leader) {

        Integer activeCompanyId = securityUtil.extractCompanyId(
                authHeader,
                leader.getCompany() != null ? leader.getCompany().getId() : null
        );

        if (activeCompanyId != null) {
            Company company = companyRepository.findById(activeCompanyId).orElse(null);
            leader.setCompany(company);
        }

        return service.addTeamLeader(leader);
    }

    @PutMapping("/{id}")
    public TeamLeader updateTeamLeader(
            @PathVariable Integer id,
            @RequestBody TeamLeader leader) {

        return service.updateTeamLeader(id, leader);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamLeader(@PathVariable Integer id) {
        service.deleteTeamLeader(id);
    }

}