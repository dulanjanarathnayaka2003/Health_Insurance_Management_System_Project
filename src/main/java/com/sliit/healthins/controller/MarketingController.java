package com.sliit.healthins.controller;

import com.sliit.healthins.dto.CampaignDTO;
import com.sliit.healthins.dto.SegmentDTO;
import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CustomerSegment;
import com.sliit.healthins.service.MarketingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
@Validated
public class MarketingController {

    private final MarketingService service;

    public MarketingController(MarketingService service) {
        this.service = service;
    }

    @PostMapping("/campaigns")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<Campaign> createCampaign(@RequestBody @Valid CampaignDTO dto) {
        return ResponseEntity.ok(service.createCampaign(dto));
    }

    @GetMapping("/campaigns")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<List<CampaignDTO>> listCampaigns() {
        return ResponseEntity.ok(service.listCampaigns());
    }

    @DeleteMapping("/campaigns/{id}")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        service.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/segments")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<CustomerSegment> defineSegment(@RequestBody @Valid SegmentDTO dto) {
        return ResponseEntity.ok(service.defineSegment(dto));
    }

    @GetMapping("/customer-count")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<Map<String, Integer>> getCustomerCount() {
        return ResponseEntity.ok(service.getCustomerCount());
    }

    @PostMapping("/send-emails")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<Map<String, Object>> sendEmailsToCustomers(@RequestBody Map<String, Long> request) {
        Long campaignId = request.get("campaignId");
        Map<String, Object> result = service.sendEmailsToCustomers(campaignId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('MARKETING')")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "ALL") String type) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        byte[] report = service.generateReport(start, end, type);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=marketing_report.pdf")
                .body(report);
    }
}