package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.security.SecurityUtil;
import com.example.demo.service.AnalyticsService;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SecurityUtil securityUtil;

    public AnalyticsController(AnalyticsService analyticsService, SecurityUtil securityUtil) {
        this.analyticsService = analyticsService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/risk-prediction")
    public ResponseEntity<Map<String, Object>> getAiRiskPrediction(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        Map<String, Object> data = analyticsService.getAiRiskPrediction(activeCompanyId);
        return ResponseEntity.ok(data);
    }
}
