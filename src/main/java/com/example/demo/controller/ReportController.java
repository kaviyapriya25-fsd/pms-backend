package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.report.*;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.ReportService;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;
    private final SecurityUtil securityUtil;

    public ReportController(ReportService reportService, SecurityUtil securityUtil) {
        this.reportService = reportService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/summary")
    public ResponseEntity<CompanyReportSummaryDTO> getReportSummary(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        CompanyReportSummaryDTO summary = reportService.getCompanyReportSummary(activeCompanyId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/employee-performance")
    public ResponseEntity<List<EmployeePerformanceDTO>> getEmployeePerformance(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        List<EmployeePerformanceDTO> list = reportService.getEmployeePerformanceReport(activeCompanyId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/leader-performance")
    public ResponseEntity<List<LeaderPerformanceDTO>> getLeaderPerformance(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        List<LeaderPerformanceDTO> list = reportService.getLeaderPerformanceReport(activeCompanyId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/project-progress")
    public ResponseEntity<List<ProjectProgressDTO>> getProjectProgress(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        List<ProjectProgressDTO> list = reportService.getProjectProgressReport(activeCompanyId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/task-completion")
    public ResponseEntity<TaskCompletionDTO> getTaskCompletion(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        TaskCompletionDTO dto = reportService.getTaskCompletionReport(activeCompanyId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(defaultValue = "EMPLOYEE") String type) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        byte[] excelBytes = reportService.generateExcelReport(type, activeCompanyId);

        String filename = "PMS_" + type.toUpperCase() + "_REPORT.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(defaultValue = "EMPLOYEE") String type) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        byte[] pdfBytes = reportService.generatePdfReport(type, activeCompanyId);

        String filename = "PMS_" + type.toUpperCase() + "_REPORT.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
