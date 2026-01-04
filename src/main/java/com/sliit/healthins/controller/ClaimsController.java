package com.sliit.healthins.controller;

import com.sliit.healthins.dto.ClaimDTO;
import com.sliit.healthins.dto.ClaimSubmissionDTO;
import com.sliit.healthins.dto.ClaimUpdateDTO;
import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.service.ClaimsService;
import com.sliit.healthins.service.CustomerSupportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@Validated
public class ClaimsController {

    private static final Logger logger = LoggerFactory.getLogger(ClaimsController.class);
    private final ClaimsService service;
    private final CustomerSupportService customerSupportService;

    public ClaimsController(ClaimsService service, CustomerSupportService customerSupportService) {
        this.service = service;
        this.customerSupportService = customerSupportService;
    }

    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('ROLE_POLICYHOLDER', 'ROLE_CLAIMS_EXECUTIVE')")
    public ResponseEntity<Claim> submitClaim(
            @RequestPart @Valid ClaimSubmissionDTO dto,
            @RequestPart MultipartFile file) {
        try {
            return ResponseEntity.ok(service.submitClaim(dto, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_CLAIMS_EXECUTIVE')")
    public ResponseEntity<List<ClaimDTO>> reviewClaims(@RequestParam(required = false) String query) {
        try {
            return ResponseEntity.ok(service.reviewClaims(query));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ROLE_CLAIMS_EXECUTIVE')")
    public ResponseEntity<byte[]> generateReport(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        try {
            byte[] report = service.generateReport(start, end);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims_report.pdf")
                    .body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/submit-by-staff")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<?> submitClaimByStaff(@RequestBody @Valid ClaimSubmissionDTO dto) {
        try {
            Claim claim = service.submitClaimByStaff(dto);
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<List<ClaimDTO>> getPendingClaims() {
        try {
            return ResponseEntity.ok(service.getPendingClaims());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<List<ClaimDTO>> getAllClaims() {
        try {
            return ResponseEntity.ok(service.getAllClaims());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/customer-bank-details/{userId}")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<?> getCustomerBankDetails(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(service.getCustomerBankDetails(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch customer bank details: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<List<ClaimDTO>> searchClaims(@RequestParam String query) {
        try {
            return ResponseEntity.ok(service.searchClaims(query));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{claimId}")
    @PreAuthorize("hasAnyRole('ROLE_CLAIMS_PROCESSING', 'ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<ClaimDTO> getClaimById(@PathVariable String claimId) {
        try {
            return ResponseEntity.ok(service.getClaimById(claimId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<byte[]> generateClaimsReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String type) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            byte[] report = service.generateClaimsReport(start, end, type);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims_report.pdf")
                    .body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{claimId}/status")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<?> updateClaimStatus(@PathVariable String claimId, @RequestBody ClaimUpdateDTO dto) {
        logger.info("=== UPDATE CLAIM STATUS REQUEST ===");
        logger.info("Claim ID: {}", claimId);
        logger.info("Status: {}", dto.getStatus());
        logger.info("Notes: {}", dto.getNotes());
        
        try {
            ClaimDTO updated = service.updateClaimStatusByClaimId(claimId, dto);
            logger.info("Claim status updated successfully: {}", updated.getStatus());
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            logger.error("Claim not found: {}", claimId, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating claim status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = customerSupportService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/customers/search")
    @PreAuthorize("hasRole('ROLE_CLAIMS_PROCESSING')")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String query) {
        try {
            List<CustomerDTO> customers = customerSupportService.searchCustomers(query);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}