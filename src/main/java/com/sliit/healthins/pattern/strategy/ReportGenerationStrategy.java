package com.sliit.healthins.pattern.strategy;

import com.itextpdf.text.pdf.PdfPTable;
import java.time.LocalDate;
import java.util.List;

/**
 * Strategy Pattern: Defines the interface for different report generation strategies
 * This allows for easy extension of new report types without modifying existing code
 */
public interface ReportGenerationStrategy {
    
    /**
     * Generates report data based on the strategy type
     * @param from Start date
     * @param to End date
     * @return List of data objects for the report
     */
    List<?> generateReportData(LocalDate from, LocalDate to);
    
    /**
     * Creates table headers specific to the report type
     * @return Array of header strings
     */
    String[] getTableHeaders();
    
    /**
     * Adds data rows to the PDF table
     * @param table PDF table to populate
     * @param data Data to add to the table
     */
    void addTableData(PdfPTable table, List<?> data);
    
    /**
     * Gets the report type name
     * @return Report type identifier
     */
    String getReportType();
}
