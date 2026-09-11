package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DashboardResponse;
import com.example.demo.dto.LeaderDashboardResponse;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.DashboardService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtil securityUtil;

    public DashboardController(DashboardService dashboardService, SecurityUtil securityUtil) {
        this.dashboardService = dashboardService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        return dashboardService.getDashboard(activeCompanyId);
    }

    @GetMapping("/dashboard/leader")
    public LeaderDashboardResponse getLeaderDashboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String leaderEmail,
            @RequestParam(required = false) String leaderName) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        return dashboardService.getLeaderDashboard(activeCompanyId, leaderEmail, leaderName);
    }

}