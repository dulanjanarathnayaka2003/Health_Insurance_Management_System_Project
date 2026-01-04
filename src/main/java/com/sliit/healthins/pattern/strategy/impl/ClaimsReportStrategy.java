package com.sliit.healthins.pattern.strategy.impl;

import com.itextpdf.text.pdf.PdfPTable;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.pattern.strategy.ReportGenerationStrategy;
import com.sliit.healthins.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Concrete Strategy: Claims Report Generation
 * Implements the strategy for generating claims reports
 */
@Component
public class ClaimsReportStrategy implements ReportGenerationStrategy {
    
    private final ClaimRepository claimRepository;
    
    @Autowired
    public ClaimsReportStrategy(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }
    
    @Override
    public List<?> generateReportData(LocalDate from, LocalDate to) {
        return claimRepository.findByClaimDateBetween(from, to);
    }
    
    @Override
    public String[] getTableHeaders() {
        return new String[]{"ID", "Policy Number", "Status", "Notes"};
    }
    
    @Override
    public void addTableData(PdfPTable table, List<?> data) {
        for (Object item : data) {
            if (item instanceof Claim claim) {
                table.addCell(claim.getId() != null ? String.valueOf(claim.getId()) : "N/A");
                table.addCell(claim.getPolicy() != null ? claim.getPolicy().getPolicyNumber() : "N/A");
                table.addCell(claim.getStatus() != null ? claim.getStatus().name() : "N/A");
                table.addCell(claim.getNotes() != null ? claim.getNotes() : "N/A");
            }
        }
    }
    
    @Override
    public String getReportType() {
        return "claims";
    }
}
