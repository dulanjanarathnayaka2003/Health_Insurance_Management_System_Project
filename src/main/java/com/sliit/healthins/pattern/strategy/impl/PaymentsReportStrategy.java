package com.sliit.healthins.pattern.strategy.impl;

import com.itextpdf.text.pdf.PdfPTable;
import com.sliit.healthins.model.Payment;
import com.sliit.healthins.pattern.strategy.ReportGenerationStrategy;
import com.sliit.healthins.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Concrete Strategy: Payments Report Generation
 * Implements the strategy for generating payments reports
 */
@Component
public class PaymentsReportStrategy implements ReportGenerationStrategy {
    
    private final PaymentRepository paymentRepository;
    
    @Autowired
    public PaymentsReportStrategy(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    @Override
    public List<?> generateReportData(LocalDate from, LocalDate to) {
        return paymentRepository.findByPaymentDateBetween(from.atStartOfDay(), to.atTime(23, 59, 59));
    }
    
    @Override
    public String[] getTableHeaders() {
        return new String[]{"ID", "Policy Number", "Status", "Amount"};
    }
    
    @Override
    public void addTableData(PdfPTable table, List<?> data) {
        for (Object item : data) {
            if (item instanceof Payment payment) {
                String idStr = Optional.of(payment)
                        .map(Payment::getId)
                        .map(String::valueOf)
                        .orElse("N/A");
                table.addCell(idStr);
                table.addCell(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : "N/A");
                table.addCell(payment.getStatus() != null ? payment.getStatus().name() : "N/A");
                table.addCell(payment.getAmount() != null ? payment.getAmount().toString() : "N/A");
            }
        }
    }
    
    @Override
    public String getReportType() {
        return "payments";
    }
}
