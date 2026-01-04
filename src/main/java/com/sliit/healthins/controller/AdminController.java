package com.sliit.healthins.controller;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.service.AdminService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.createUser(dto));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.updateUser(id, dto));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monitor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricsDTO> getSystemMetrics() {
        return ResponseEntity.ok(service.getSystemMetrics());
    }

    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigDTO> updateConfig(@RequestBody @Valid ConfigDTO dto) {
        return ResponseEntity.ok(service.updateConfig(dto));
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateAuditLog(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "ALL") String type) throws IOException {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        byte[] report = service.generateAuditLog(start, end, type);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit_log.pdf")
                .body(report);
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigDTO> getConfig() {
        return ResponseEntity.ok(service.getConfig());
    }

    @PostMapping("/config/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemConfigDTO> saveSystemConfig(@RequestBody @Valid SystemConfigDTO dto) {
        return ResponseEntity.ok(service.saveSystemConfig(dto));
    }

    @PostMapping("/config/security")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecurityConfigDTO> saveSecurityConfig(@RequestBody @Valid SecurityConfigDTO dto) {
        return ResponseEntity.ok(service.saveSecurityConfig(dto));
    }

    @PostMapping("/config/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmailConfigDTO> saveEmailConfig(@RequestBody @Valid EmailConfigDTO dto) {
        return ResponseEntity.ok(service.saveEmailConfig(dto));
    }

    @PostMapping("/config/api")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiConfigDTO> saveApiConfig(@RequestBody @Valid ApiConfigDTO dto) {
        return ResponseEntity.ok(service.saveApiConfig(dto));
    }

    @PostMapping("/config/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentConfigDTO> savePaymentConfig(@RequestBody @Valid PaymentConfigDTO dto) {
        return ResponseEntity.ok(service.savePaymentConfig(dto));
    }
}