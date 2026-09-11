package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.report.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final TeamLeaderRepository teamLeaderRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;

    public ReportService(EmployeeRepository employeeRepository,
                         TeamLeaderRepository teamLeaderRepository,
                         ProjectRepository projectRepository,
                         TaskRepository taskRepository,
                         CompanyRepository companyRepository) {

        this.employeeRepository = employeeRepository;
        this.teamLeaderRepository = teamLeaderRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.companyRepository = companyRepository;
    }

    // ===============================================
    // 1. EMPLOYEE PERFORMANCE REPORT
    // ===============================================
    public List<EmployeePerformanceDTO> getEmployeePerformanceReport(Integer companyId) {
        if (companyId == null) return List.of();

        List<Employee> employees = employeeRepository.findByCompany_Id(companyId);
        List<Task> companyTasks = taskRepository.findByCompany_Id(companyId);

        List<EmployeePerformanceDTO> reportList = new ArrayList<>();

        for (Employee emp : employees) {
            String empEmail = emp.getEmail();
            String empName = emp.getName();

            // Match task assigned by email or name
            List<Task> empTasks = companyTasks.stream()
                    .filter(t -> (t.getEmployee() != null &&
                            (t.getEmployee().equalsIgnoreCase(empEmail) || t.getEmployee().equalsIgnoreCase(empName))))
                    .collect(Collectors.toList());

            int total = empTasks.size();
            int completed = (int) empTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            int inProgress = (int) empTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            int todo = (int) empTasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).count();
            int overdue = (int) empTasks.stream()
                    .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()))
                    .count();

            double rate = total > 0 ? Math.round(((double) completed / total) * 100.0 * 10.0) / 10.0 : 0.0;

            EmployeePerformanceDTO dto = new EmployeePerformanceDTO();
            dto.setEmployeeId(emp.getId());
            dto.setEmployeeName(emp.getName());
            dto.setEmail(emp.getEmail());
            dto.setDesignation(emp.getDesignation() != null ? emp.getDesignation() : "Staff");
            dto.setTeamLeader(emp.getTeamLeader() != null ? emp.getTeamLeader() : "Unassigned");
            dto.setAssignedProject(emp.getProject() != null ? emp.getProject() : "Unassigned");
            dto.setTotalTasks(total);
            dto.setCompletedTasks(completed);
            dto.setInProgressTasks(inProgress);
            dto.setTodoTasks(todo);
            dto.setOverdueTasks(overdue);
            dto.setCompletionRate(rate);
            dto.setStatus(emp.getStatus() != null ? emp.getStatus() : "ACTIVE");

            reportList.add(dto);
        }

        return reportList;
    }

    // ===============================================
    // 2. LEADER PERFORMANCE REPORT
    // ===============================================
    public List<LeaderPerformanceDTO> getLeaderPerformanceReport(Integer companyId) {
        if (companyId == null) return List.of();

        List<TeamLeader> leaders = teamLeaderRepository.findByCompany_Id(companyId);
        List<Employee> allEmployees = employeeRepository.findByCompany_Id(companyId);
        List<Task> companyTasks = taskRepository.findByCompany_Id(companyId);

        List<LeaderPerformanceDTO> reportList = new ArrayList<>();

        for (TeamLeader leader : leaders) {
            String leaderEmail = leader.getEmail();
            String leaderName = leader.getName();

            // Count team members under this leader
            int teamCount = (int) allEmployees.stream()
                    .filter(e -> e.getTeamLeader() != null &&
                            (e.getTeamLeader().equalsIgnoreCase(leaderEmail) || e.getTeamLeader().equalsIgnoreCase(leaderName)))
                    .count();

            // Match team tasks by teamLeader email/name
            List<Task> leaderTasks = companyTasks.stream()
                    .filter(t -> t.getTeamLeader() != null &&
                            (t.getTeamLeader().equalsIgnoreCase(leaderEmail) || t.getTeamLeader().equalsIgnoreCase(leaderName)))
                    .collect(Collectors.toList());

            int total = leaderTasks.size();
            int completed = (int) leaderTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            int inProgress = (int) leaderTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            double rate = total > 0 ? Math.round(((double) completed / total) * 100.0 * 10.0) / 10.0 : 0.0;

            LeaderPerformanceDTO dto = new LeaderPerformanceDTO();
            dto.setLeaderId(leader.getId());
            dto.setLeaderName(leader.getName());
            dto.setEmail(leader.getEmail());
            dto.setDepartment(leader.getDepartment() != null ? leader.getDepartment() : "General");
            dto.setAssignedProject(leader.getProject() != null ? leader.getProject() : "Unassigned");
            dto.setTeamEmployeeCount(teamCount);
            dto.setTotalTeamTasks(total);
            dto.setCompletedTeamTasks(completed);
            dto.setInProgressTeamTasks(inProgress);
            dto.setTeamCompletionRate(rate);
            dto.setStatus(leader.getStatus() != null ? leader.getStatus() : "ACTIVE");

            reportList.add(dto);
        }

        return reportList;
    }

    // ===============================================
    // 3. PROJECT PROGRESS REPORT
    // ===============================================
    public List<ProjectProgressDTO> getProjectProgressReport(Integer companyId) {
        if (companyId == null) return List.of();

        List<Project> projects = projectRepository.findByCompany_Id(companyId);
        List<Task> companyTasks = taskRepository.findByCompany_Id(companyId);

        List<ProjectProgressDTO> reportList = new ArrayList<>();

        for (Project proj : projects) {
            String projName = proj.getProjectName();

            List<Task> projTasks = companyTasks.stream()
                    .filter(t -> t.getProject() != null && t.getProject().equalsIgnoreCase(projName))
                    .collect(Collectors.toList());

            int total = projTasks.size();
            int completed = (int) projTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
            int inProgress = (int) projTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
            int todo = (int) projTasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).count();

            double progress = total > 0 ? Math.round(((double) completed / total) * 100.0 * 10.0) / 10.0 : 0.0;

            ProjectProgressDTO dto = new ProjectProgressDTO();
            dto.setProjectId(proj.getId());
            dto.setProjectName(proj.getProjectName());
            dto.setLeaderName(proj.getLeader() != null ? proj.getLeader() : "Unassigned");
            dto.setPriority(proj.getPriority() != null ? proj.getPriority() : "MEDIUM");
            dto.setStatus(proj.getStatus() != null ? proj.getStatus() : "ACTIVE");
            dto.setStartDate(proj.getStartDate() != null ? proj.getStartDate().toString() : "TBD");
            dto.setEndDate(proj.getEndDate() != null ? proj.getEndDate().toString() : "TBD");
            dto.setTotalTasks(total);
            dto.setCompletedTasks(completed);
            dto.setInProgressTasks(inProgress);
            dto.setTodoTasks(todo);
            dto.setProgressPercentage(progress);

            reportList.add(dto);
        }

        return reportList;
    }

    // ===============================================
    // 4. TASK COMPLETION REPORT
    // ===============================================
    public TaskCompletionDTO getTaskCompletionReport(Integer companyId) {
        if (companyId == null) return new TaskCompletionDTO();

        List<Task> companyTasks = taskRepository.findByCompany_Id(companyId);

        int total = companyTasks.size();
        int completed = (int) companyTasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
        int inProgress = (int) companyTasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
        int todo = (int) companyTasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).count();
        int overdue = (int) companyTasks.stream()
                .filter(t -> !"COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()))
                .count();

        double rate = total > 0 ? Math.round(((double) completed / total) * 100.0 * 10.0) / 10.0 : 0.0;

        int high = (int) companyTasks.stream().filter(t -> "HIGH".equalsIgnoreCase(t.getPriority()) || "URGENT".equalsIgnoreCase(t.getPriority())).count();
        int medium = (int) companyTasks.stream().filter(t -> "MEDIUM".equalsIgnoreCase(t.getPriority())).count();
        int low = (int) companyTasks.stream().filter(t -> "LOW".equalsIgnoreCase(t.getPriority())).count();

        // Build 6-month trend sequence
        List<TaskCompletionDTO.MonthlyTrendPoint> trends = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            String monthLabel = monthDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));

            // Filter tasks created or completed in that month
            int cCount = (int) companyTasks.stream()
                    .filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()) && t.getDueDate() != null && t.getDueDate().getMonth() == monthDate.getMonth())
                    .count();
            int crCount = (int) companyTasks.stream()
                    .filter(t -> t.getStartDate() != null && t.getStartDate().getMonth() == monthDate.getMonth())
                    .count();

            trends.add(new TaskCompletionDTO.MonthlyTrendPoint(monthLabel, cCount, crCount));
        }

        TaskCompletionDTO dto = new TaskCompletionDTO();
        dto.setTotalTasks(total);
        dto.setCompletedTasks(completed);
        dto.setInProgressTasks(inProgress);
        dto.setTodoTasks(todo);
        dto.setOverdueTasks(overdue);
        dto.setOverallCompletionRate(rate);
        dto.setHighPriorityCount(high);
        dto.setMediumPriorityCount(medium);
        dto.setLowPriorityCount(low);
        dto.setMonthlyTrends(trends);

        return dto;
    }

    // ===============================================
    // 5. COMPANY REPORT SUMMARY
    // ===============================================
    public CompanyReportSummaryDTO getCompanyReportSummary(Integer companyId) {
        CompanyReportSummaryDTO summary = new CompanyReportSummaryDTO();
        summary.setCompanyId(companyId);

        if (companyId != null) {
            companyRepository.findById(companyId).ifPresent(c -> summary.setCompanyName(c.getCompanyName()));
        }
        if (summary.getCompanyName() == null) {
            summary.setCompanyName("Enterprise Corp");
        }

        List<EmployeePerformanceDTO> empList = getEmployeePerformanceReport(companyId);
        List<LeaderPerformanceDTO> leaderList = getLeaderPerformanceReport(companyId);
        List<ProjectProgressDTO> projList = getProjectProgressReport(companyId);
        TaskCompletionDTO taskReport = getTaskCompletionReport(companyId);

        summary.setEmployeePerformanceList(empList);
        summary.setLeaderPerformanceList(leaderList);
        summary.setProjectProgressList(projList);
        summary.setTaskCompletionReport(taskReport);

        summary.setTotalEmployees(empList.size());
        summary.setTotalTeamLeaders(leaderList.size());
        summary.setTotalProjects(projList.size());
        summary.setTotalTasks(taskReport.getTotalTasks());

        double avgProjectProgress = projList.stream().mapToDouble(ProjectProgressDTO::getProgressPercentage).average().orElse(0.0);
        summary.setOverallProjectCompletionRate(Math.round(avgProjectProgress * 10.0) / 10.0);

        return summary;
    }

    // ===============================================
    // 6. GENERATE EXCEL REPORT (.xlsx)
    // ===============================================
    public byte[] generateExcelReport(String reportType, Integer companyId) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Report Data");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title Row
            Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("PMS REPORT - " + (reportType != null ? reportType.toUpperCase() : "SUMMARY"));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            String type = reportType != null ? reportType.toUpperCase() : "EMPLOYEE";
            Row headerRow = sheet.createRow(2);

            int rowIdx = 3;

            switch (type) {
                case "LEADER" -> {
                    String[] headers = {"ID", "Leader Name", "Email", "Department", "Assigned Project", "Team Members", "Completion Rate (%)"};
                    for (int i = 0; i < headers.length; i++) {
                        org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerStyle);
                    }
                    List<LeaderPerformanceDTO> data = getLeaderPerformanceReport(companyId);
                    for (LeaderPerformanceDTO d : data) {
                        Row r = sheet.createRow(rowIdx++);
                        r.createCell(0).setCellValue(d.getLeaderId());
                        r.createCell(1).setCellValue(d.getLeaderName());
                        r.createCell(2).setCellValue(d.getEmail());
                        r.createCell(3).setCellValue(d.getDepartment());
                        r.createCell(4).setCellValue(d.getAssignedProject());
                        r.createCell(5).setCellValue(d.getTeamEmployeeCount());
                        r.createCell(6).setCellValue(d.getTeamCompletionRate());
                    }
                }
                case "PROJECT" -> {
                    String[] headers = {"ID", "Project Name", "Leader", "Priority", "Status", "Start Date", "Progress (%)"};
                    for (int i = 0; i < headers.length; i++) {
                        org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerStyle);
                    }
                    List<ProjectProgressDTO> data = getProjectProgressReport(companyId);
                    for (ProjectProgressDTO d : data) {
                        Row r = sheet.createRow(rowIdx++);
                        r.createCell(0).setCellValue(d.getProjectId());
                        r.createCell(1).setCellValue(d.getProjectName());
                        r.createCell(2).setCellValue(d.getLeaderName());
                        r.createCell(3).setCellValue(d.getPriority());
                        r.createCell(4).setCellValue(d.getStatus());
                        r.createCell(5).setCellValue(d.getStartDate());
                        r.createCell(6).setCellValue(d.getProgressPercentage());
                    }
                }
                case "TASK" -> {
                    String[] headers = {"Metric", "Count / Value"};
                    for (int i = 0; i < headers.length; i++) {
                        org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerStyle);
                    }
                    TaskCompletionDTO d = getTaskCompletionReport(companyId);
                    Row r1 = sheet.createRow(rowIdx++); r1.createCell(0).setCellValue("Total Tasks"); r1.createCell(1).setCellValue(d.getTotalTasks());
                    Row r2 = sheet.createRow(rowIdx++); r2.createCell(0).setCellValue("Completed Tasks"); r2.createCell(1).setCellValue(d.getCompletedTasks());
                    Row r3 = sheet.createRow(rowIdx++); r3.createCell(0).setCellValue("In Progress Tasks"); r3.createCell(1).setCellValue(d.getInProgressTasks());
                    Row r4 = sheet.createRow(rowIdx++); r4.createCell(0).setCellValue("Todo Tasks"); r4.createCell(1).setCellValue(d.getTodoTasks());
                    Row r5 = sheet.createRow(rowIdx++); r5.createCell(0).setCellValue("Overdue Tasks"); r5.createCell(1).setCellValue(d.getOverdueTasks());
                    Row r6 = sheet.createRow(rowIdx++); r6.createCell(0).setCellValue("Overall Completion Rate (%)"); r6.createCell(1).setCellValue(d.getOverallCompletionRate());
                }
                default -> { // EMPLOYEE
                    String[] headers = {"ID", "Employee Name", "Email", "Designation", "Team Leader", "Completed Tasks", "Completion Rate (%)"};
                    for (int i = 0; i < headers.length; i++) {
                        org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                        c.setCellValue(headers[i]);
                        c.setCellStyle(headerStyle);
                    }
                    List<EmployeePerformanceDTO> data = getEmployeePerformanceReport(companyId);
                    for (EmployeePerformanceDTO d : data) {
                        Row r = sheet.createRow(rowIdx++);
                        r.createCell(0).setCellValue(d.getEmployeeId());
                        r.createCell(1).setCellValue(d.getEmployeeName());
                        r.createCell(2).setCellValue(d.getEmail());
                        r.createCell(3).setCellValue(d.getDesignation());
                        r.createCell(4).setCellValue(d.getTeamLeader());
                        r.createCell(5).setCellValue(d.getCompletedTasks() + " / " + d.getTotalTasks());
                        r.createCell(6).setCellValue(d.getCompletionRate());
                    }
                }
            }

            for (int col = 0; col < 7; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            System.err.println("[ReportService] Excel export failed: " + e.getMessage());
            return new byte[0];
        }
    }

    // ===============================================
    // 7. GENERATE PDF REPORT (.pdf)
    // ===============================================
    public byte[] generatePdfReport(String reportType, Integer companyId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL, new java.awt.Color(30, 27, 75));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new java.awt.Color(100, 116, 139));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, java.awt.Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new java.awt.Color(51, 65, 85));

            String type = reportType != null ? reportType.toUpperCase() : "EMPLOYEE";

            // Title
            Paragraph title = new Paragraph("PROJECT MANAGEMENT SYSTEM REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Report Category: " + type + " PERFORMANCE | Date: " + LocalDate.now(), subTitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            PdfPTable table;

            switch (type) {
                case "LEADER" -> {
                    table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1, 3, 3, 2, 3, 2});

                    String[] headers = {"ID", "Leader Name", "Department", "Assigned Project", "Team Tasks", "Rate %"};
                    for (String h : headers) {
                        PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                        c.setBackgroundColor(new java.awt.Color(67, 56, 202));
                        c.setPadding(6);
                        table.addCell(c);
                    }

                    List<LeaderPerformanceDTO> data = getLeaderPerformanceReport(companyId);
                    for (LeaderPerformanceDTO d : data) {
                        table.addCell(new Phrase("#" + d.getLeaderId(), cellFont));
                        table.addCell(new Phrase(d.getLeaderName(), cellFont));
                        table.addCell(new Phrase(d.getDepartment(), cellFont));
                        table.addCell(new Phrase(d.getAssignedProject(), cellFont));
                        table.addCell(new Phrase(d.getCompletedTeamTasks() + "/" + d.getTotalTeamTasks(), cellFont));
                        table.addCell(new Phrase(d.getTeamCompletionRate() + "%", cellFont));
                    }
                }
                case "PROJECT" -> {
                    table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1, 3, 3, 2, 2, 2});

                    String[] headers = {"ID", "Project Name", "Leader", "Priority", "Status", "Progress"};
                    for (String h : headers) {
                        PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                        c.setBackgroundColor(new java.awt.Color(67, 56, 202));
                        c.setPadding(6);
                        table.addCell(c);
                    }

                    List<ProjectProgressDTO> data = getProjectProgressReport(companyId);
                    for (ProjectProgressDTO d : data) {
                        table.addCell(new Phrase("#" + d.getProjectId(), cellFont));
                        table.addCell(new Phrase(d.getProjectName(), cellFont));
                        table.addCell(new Phrase(d.getLeaderName(), cellFont));
                        table.addCell(new Phrase(d.getPriority(), cellFont));
                        table.addCell(new Phrase(d.getStatus(), cellFont));
                        table.addCell(new Phrase(d.getProgressPercentage() + "%", cellFont));
                    }
                }
                default -> { // EMPLOYEE
                    table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1, 3, 3, 3, 2, 2});

                    String[] headers = {"ID", "Employee Name", "Designation", "Team Leader", "Tasks", "Rate %"};
                    for (String h : headers) {
                        PdfPCell c = new PdfPCell(new Phrase(h, headerFont));
                        c.setBackgroundColor(new java.awt.Color(67, 56, 202));
                        c.setPadding(6);
                        table.addCell(c);
                    }

                    List<EmployeePerformanceDTO> data = getEmployeePerformanceReport(companyId);
                    for (EmployeePerformanceDTO d : data) {
                        table.addCell(new Phrase("#" + d.getEmployeeId(), cellFont));
                        table.addCell(new Phrase(d.getEmployeeName(), cellFont));
                        table.addCell(new Phrase(d.getDesignation(), cellFont));
                        table.addCell(new Phrase(d.getTeamLeader(), cellFont));
                        table.addCell(new Phrase(d.getCompletedTasks() + "/" + d.getTotalTasks(), cellFont));
                        table.addCell(new Phrase(d.getCompletionRate() + "%", cellFont));
                    }
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            System.err.println("[ReportService] PDF export failed: " + e.getMessage());
            return new byte[0];
        }
    }
}
