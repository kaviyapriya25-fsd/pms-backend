package com.example.demo.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final JwtService jwtService;

    public SecurityUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Integer extractCompanyId(String authHeader, Integer fallbackCompanyId) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Integer companyIdFromJwt = jwtService.extractCompanyId(token);
                if (companyIdFromJwt != null) {
                    return companyIdFromJwt;
                }
            } catch (Exception e) {
                // Fallthrough to fallback if token parsing fails
            }
        }
        return fallbackCompanyId;
    }

    public String extractUserEmail(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                return jwtService.extractEmail(token);
            } catch (Exception e) {
                // Ignore
            }
        }
        return null;
    }
}
