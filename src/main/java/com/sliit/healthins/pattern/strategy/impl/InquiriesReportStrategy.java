package com.sliit.healthins.pattern.strategy.impl;

import com.itextpdf.text.pdf.PdfPTable;
import com.sliit.healthins.model.Inquiry;
import com.sliit.healthins.pattern.strategy.ReportGenerationStrategy;
import com.sliit.healthins.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Concrete Strategy: Inquiries Report Generation
 * Implements the strategy for generating inquiries reports
 */
@Component
public class InquiriesReportStrategy implements ReportGenerationStrategy {
    
    private final InquiryRepository inquiryRepository;
    
    @Autowired
    public InquiriesReportStrategy(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }
    
    @Override
    public List<?> generateReportData(LocalDate from, LocalDate to) {
        return inquiryRepository.findByResolutionDateBetween(from, to);
    }
    
    @Override
    public String[] getTableHeaders() {
        return new String[]{"ID", "Type", "Status", "Resolution Date"};
    }
    
    @Override
    public void addTableData(PdfPTable table, List<?> data) {
        for (Object item : data) {
            if (item instanceof Inquiry inquiry) {
                table.addCell(inquiry.getId() != null ? String.valueOf(inquiry.getId()) : "N/A");
                table.addCell(inquiry.getType() != null ? inquiry.getType() : "N/A");
                table.addCell(inquiry.getStatus() != null ? inquiry.getStatus().name() : "N/A");
                table.addCell(inquiry.getResolutionDate() != null ? inquiry.getResolutionDate().toString() : "N/A");
            }
        }
    }
    
    @Override
    public String getReportType() {
        return "inquiries";
    }
}
