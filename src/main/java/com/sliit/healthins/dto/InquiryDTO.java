package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InquiryDTO {
    private Long id;
    private Long customerId;
    private String type;
    private String title;
    private String description;
    private String subject; // alias for title
    private String status;
    private LocalDate resolutionDate;
    private LocalDateTime createdAt;
    private String customerName;
    private String response;

    public InquiryDTO(Long id, Long customerId, String type, String description, String status, LocalDate resolutionDate) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.resolutionDate = resolutionDate;
    }
}