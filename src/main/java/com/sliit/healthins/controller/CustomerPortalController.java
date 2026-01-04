package com.sliit.healthins.controller;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.model.User;
import com.sliit.healthins.service.CustomerPortalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.sliit.healthins.config.CustomUserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Validated
public class CustomerPortalController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerPortalController.class);
    private final CustomerPortalService service;

    public CustomerPortalController(CustomerPortalService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Getting profile for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            logger.debug("User ID: {}, Role: {}", user.getId(), user.getRole());
            ProfileDTO profile = service.getProfile(user.getId());
            logger.info("Successfully retrieved profile for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error getting profile for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @PutMapping("/update-profile")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileDTO profileDTO,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Updating profile for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            ProfileDTO updatedProfile = service.updateProfile(user.getId(), profileDTO);
            logger.info("Successfully updated profile for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error updating profile for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @PutMapping("/update-bank-info")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<ProfileDTO> updateBankInfo(@RequestBody ProfileDTO profileDTO,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Updating bank info for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            ProfileDTO updatedProfile = service.updateBankInfo(user.getId(), profileDTO);
            logger.info("Successfully updated bank info for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error updating bank info for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/policies")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<PolicyDetailsDTO>> getPolicies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(service.getPolicies(user.getId()));
    }

    @GetMapping("/claims/approved")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<ClaimDTO>> getApprovedClaims(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(service.getApprovedClaims(user.getId()));
    }

    @GetMapping("/payments/next-due")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<PaymentReminderDTO> getNextPaymentDue(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(service.getNextPaymentDue(user.getId()));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<PaymentDTO>> getPayments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Getting payments for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            List<PaymentDTO> payments = service.getPayments(user.getId());
            logger.info("Successfully retrieved {} payments", payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            logger.error("Error getting payments for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentDTO paymentDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Processing payment for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            PaymentDTO result = service.processPayment(paymentDTO, user.getId());
            logger.info("Successfully processed payment");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error processing payment for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/claims")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<Claim> submitClaim(@RequestBody @Valid ClaimSubmissionDTO dto,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Submitting claim for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            logger.debug("Claim details - Policy ID: {}, Amount: {}, Description: {}", 
                dto.getPolicyId(), dto.getAmount(), dto.getDescription());
            
            Claim claim = service.submitClaim(dto, user.getId());
            logger.info("Successfully submitted claim with ID: {}", claim.getId());
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            logger.error("Error submitting claim for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/claims")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<Claim>> getClaims(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(service.getClaims(user.getId()));
    }

    @GetMapping("/billing")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<BillingHistoryDTO>> getBilling(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(service.getBillingHistory(user.getId()));
    }

    @PutMapping("/contact")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<Void> updateContact(@RequestBody @Valid ContactUpdateDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        service.updateContact(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inquiries")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<InquiryDTO> submitInquiry(@RequestBody @Valid InquiryDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Submitting inquiry for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            InquiryDTO inquiry = service.submitInquiry(dto, user.getId());
            logger.info("Successfully submitted inquiry");
            return ResponseEntity.ok(inquiry);
        } catch (Exception e) {
            logger.error("Error submitting inquiry for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/inquiries")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<InquiryDTO>> getInquiries(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Getting inquiries for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            List<InquiryDTO> inquiries = service.getInquiries(user.getId());
            logger.info("Successfully retrieved {} inquiries", inquiries.size());
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            logger.error("Error getting inquiries for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/available-policies")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<List<PolicyInfoDTO>> getAvailablePolicies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Getting available policies for user: {}", userDetails.getUsername());
            List<PolicyInfoDTO> policies = service.getAvailablePolicies();
            logger.info("Successfully retrieved {} available policies", policies.size());
            return ResponseEntity.ok(policies);
        } catch (Exception e) {
            logger.error("Error getting available policies", e);
            throw e;
        }
    }

    @PostMapping("/purchase-policy")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_POLICYHOLDER')")
    public ResponseEntity<PolicyDetailsDTO> purchasePolicy(@RequestBody @Valid PolicyPurchaseDTO purchaseDTO,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            logger.info("Processing policy purchase for user: {}", userDetails.getUsername());
            User user = userDetails.getUser();
            PolicyDetailsDTO policy = service.purchasePolicy(user.getId(), purchaseDTO);
            logger.info("Successfully purchased policy: {}", policy.getPolicyNumber());
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            logger.error("Error purchasing policy for user: " + userDetails.getUsername(), e);
            throw e;
        }
    }
}