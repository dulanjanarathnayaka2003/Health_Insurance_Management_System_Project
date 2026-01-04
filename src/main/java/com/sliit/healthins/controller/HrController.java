package com.sliit.healthins.controller;

import com.sliit.healthins.dto.EmployeeDTO;
import com.sliit.healthins.dto.PayrollDTO;
import com.sliit.healthins.dto.ReviewDTO;
import com.sliit.healthins.dto.UserDTO;
import com.sliit.healthins.model.Employee;
import com.sliit.healthins.model.Payroll;
import com.sliit.healthins.model.PerformanceReview;
import com.sliit.healthins.service.HrService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@Validated
public class HrController {

    private final HrService service;

    public HrController(HrService service) {
        this.service = service;
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<UserDTO>> getCustomers() {
        return ResponseEntity.ok(service.getCustomers());
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<UserDTO> createCustomer(@RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.createCustomer(dto));
    }

    @PutMapping("/customers/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<UserDTO> updateCustomer(@PathVariable Long id, @RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.updateCustomer(id, dto));
    }

    @DeleteMapping("/customers/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid EmployeeDTO dto) {
        return ResponseEntity.ok(service.createEmployee(dto));
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDTO dto) {
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployee(id));
    }
    
    @GetMapping("/search-employee-by-user/{userId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeDTO> searchEmployeeByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.searchEmployeeByUserId(userId));
    }
    
    @GetMapping("/employees-list")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<EmployeeDTO>> getInternalStaffList() {
        return ResponseEntity.ok(service.getAllInternalStaff());
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<PayrollDTO>> getAllPayrolls() {
        return ResponseEntity.ok(service.getAllPayrolls());
    }

    @GetMapping("/payroll/search")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<PayrollDTO>> searchPayrolls(@RequestParam Long employeeId) {
        return ResponseEntity.ok(service.searchPayrollsByEmployee(employeeId));
    }

    @PutMapping("/payroll/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PayrollDTO> updatePayrollStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.updatePayrollStatus(id, request.get("status")));
    }

    @PostMapping("/payroll")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<PayrollDTO> processPayroll(@RequestBody @Valid PayrollDTO dto) {
        return ResponseEntity.ok(service.processPayroll(dto));
    }

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<PerformanceReview> addReview(@RequestBody @Valid ReviewDTO dto) {
        return ResponseEntity.ok(service.addReview(dto));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String startDate, 
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "ALL") String type) throws IOException {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        byte[] report = service.generateHrReport(start, end, type);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hr_report.pdf")
                .body(report);
    }
}