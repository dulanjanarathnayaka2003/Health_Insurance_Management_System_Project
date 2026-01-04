package com.sliit.healthins.controller;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.service.CustomerSupportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/customer-support")
@Validated
public class CustomerSupportController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSupportController.class);
    private final CustomerSupportService customerSupportService;

    public CustomerSupportController(CustomerSupportService customerSupportService) {
        this.customerSupportService = customerSupportService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        logger.info("Getting dashboard stats");
        return ResponseEntity.ok(customerSupportService.getDashboardStats());
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        logger.info("Getting all customers");
        List<CustomerDTO> customers = customerSupportService.getAllCustomers();
        logger.info("Returning {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/search")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String query) {
        logger.info("Searching customers with query: {}", query);
        List<CustomerDTO> customers = customerSupportService.searchCustomers(query);
        logger.info("Found {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        logger.info("Getting customer with ID: {}", id);
        return ResponseEntity.ok(customerSupportService.getCustomer(id));
    }

    @PutMapping("/customers/{id}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDTO customerDTO) {
        logger.info("Updating customer with ID: {}", id);
        CustomerDTO updated = customerSupportService.updateCustomer(id, customerDTO);
        logger.info("Successfully updated customer ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/policies/{policyNumber}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<PolicyDetailsDTO> updatePolicy(@PathVariable String policyNumber, @RequestBody @Valid PolicyDetailsDTO policyDTO) {
        logger.info("Updating policy: {}", policyNumber);
        return ResponseEntity.ok(customerSupportService.updatePolicy(policyNumber, policyDTO));
    }

    @PostMapping("/reminders")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<String> sendPaymentReminder(@RequestBody @Valid PaymentReminderDTO reminderDTO) {
        logger.info("Sending payment reminder");
        customerSupportService.sendPaymentReminder(reminderDTO);
        return ResponseEntity.ok("Reminder sent successfully at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @GetMapping("/claims")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<ClaimUpdateDTO>> getClaims() {
        logger.info("Getting all claims");
        return ResponseEntity.ok(customerSupportService.getClaims());
    }

    @PutMapping("/claims/{id}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<ClaimUpdateDTO> updateClaimStatus(@PathVariable Long id, @RequestBody @Valid ClaimUpdateDTO claimDTO) {
        logger.info("Updating claim status for ID: {}", id);
        return ResponseEntity.ok(customerSupportService.updateClaimStatus(id, claimDTO));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<?> generateReport(@RequestParam String reportType, @RequestParam String fromDate, @RequestParam String toDate) {
        logger.info("Generating report: {} from {} to {}", reportType, fromDate, toDate);
        
        try {
            byte[] report = customerSupportService.generateReport(reportType, fromDate, toDate);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            logger.info("Report generated successfully, size: {} bytes", report.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportType.replace(" ", "_") + "_report_" + timestamp + ".pdf")
                    .body(report);
                    
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
                    
        } catch (Exception e) {
            logger.error("Error generating report: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Failed to generate report: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/inquiries/pending")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<InquiryDTO>> getPendingInquiries() {
        logger.info("Getting pending inquiries");
        return ResponseEntity.ok(customerSupportService.getPendingInquiries());
    }

    @GetMapping("/inquiries/all")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<InquiryDTO>> getAllInquiries() {
        logger.info("Getting all inquiries");
        return ResponseEntity.ok(customerSupportService.getAllInquiries());
    }

    @GetMapping("/inquiries/search")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<InquiryDTO>> searchInquiries(@RequestParam String query) {
        logger.info("Searching inquiries with query: {}", query);
        return ResponseEntity.ok(customerSupportService.searchInquiries(query));
    }

    @PutMapping("/inquiries/{id}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<InquiryDTO> updateInquiry(@PathVariable Long id, @RequestBody @Valid InquiryDTO inquiryDTO) {
        logger.info("Updating inquiry ID: {}", id);
        return ResponseEntity.ok(customerSupportService.updateInquiry(id, inquiryDTO));
    }

    @GetMapping("/policy-info")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<PolicyDetailsDTO>> getAllPolicyInfo() {
        logger.info("Getting all policy info/coverage types");
        return ResponseEntity.ok(customerSupportService.getAllPolicyInfo());
    }

    @GetMapping("/customers/{id}/policies")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<PolicyDetailsDTO>> getCustomerPolicies(@PathVariable Long id) {
        logger.info("Getting policies for customer ID: {}", id);
        return ResponseEntity.ok(customerSupportService.getCustomerPolicies(id));
    }

    @PostMapping("/policies")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<PolicyDetailsDTO> addPolicy(@RequestBody @Valid PolicyDetailsDTO policyDTO) {
        logger.info("Adding new policy for customer ID: {}", policyDTO.getCustomerId());
        return ResponseEntity.ok(customerSupportService.addPolicy(policyDTO));
    }

    @DeleteMapping("/policies/{policyNumber}")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<Void> deletePolicy(@PathVariable String policyNumber) {
        logger.info("Deleting policy: {}", policyNumber);
        customerSupportService.deletePolicy(policyNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<PaymentReminderDTO>> getAllPayments() {
        logger.info("Getting all payments for reminder management");
        return ResponseEntity.ok(customerSupportService.getAllPayments());
    }

    @GetMapping("/payments/search")
    @PreAuthorize("hasRole('CUSTOMER_SERVICE')")
    public ResponseEntity<List<PaymentReminderDTO>> searchPayments(@RequestParam String query) {
        logger.info("Searching payments with query: {}", query);
        return ResponseEntity.ok(customerSupportService.searchPayments(query));
    }
}