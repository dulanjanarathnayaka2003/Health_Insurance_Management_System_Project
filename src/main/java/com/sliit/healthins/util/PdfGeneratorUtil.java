package com.sliit.healthins.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class PdfGeneratorUtil {
    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorUtil.class);

    /**
     * Generates a PDF document with the given content and customizable title.
     *
     * @param title   The title of the PDF document
     * @param content The text content to include in the PDF
     * @return Byte array of the generated PDF
     * @throws IOException if PDF generation fails
     * @throws IllegalArgumentException if title or content is null or empty
     */
    public byte[] generatePdf(String title, String content) throws IOException {
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and content cannot be null or empty");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph(title));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now()));
            document.add(new Paragraph(content));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("PDF generation failed", e);
            throw new IOException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}