package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Company;
import com.example.demo.entity.Employee;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.EmployeeService;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService service;
    private final SecurityUtil securityUtil;
    private final CompanyRepository companyRepository;

    public EmployeeController(EmployeeService service,
                               SecurityUtil securityUtil,
                               CompanyRepository companyRepository) {

        this.service = service;
        this.securityUtil = securityUtil;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<Employee> getAllEmployees(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        return service.getAllEmployees(activeCompanyId);
    }

    @PostMapping
    public Employee addEmployee(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Employee employee) {

        Integer activeCompanyId = securityUtil.extractCompanyId(
                authHeader,
                employee.getCompany() != null ? employee.getCompany().getId() : null
        );

        if (activeCompanyId != null) {
            Company company = companyRepository.findById(activeCompanyId).orElse(null);
            employee.setCompany(company);
        }

        return service.addEmployee(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(
            @PathVariable Integer id,
            @RequestBody Employee employee) {

        return service.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Integer id) {
        service.deleteEmployee(id);
    }

}