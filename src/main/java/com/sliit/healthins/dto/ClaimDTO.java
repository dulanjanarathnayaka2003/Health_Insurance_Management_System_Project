package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClaimDTO {
    private Long id;
    private String claimId;
    private String status;
    private Double amount;
    private String documentPath;
    private LocalDate claimDate;
    private String notes;
    private String policyNumber;
    private String policyType;
    private Long userId;
    private String userName;
    private String description;
    private java.time.LocalDateTime createdAt;
}