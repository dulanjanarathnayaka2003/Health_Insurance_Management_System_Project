package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PolicyDetailsDTO {
    private Long id;
    private Long customerId;
    private String policyNumber;
    private String status;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverage;
    private String coverageType;
    private String description;
    private String benefits;
    private String coverageLimit;
    private int totalClaims;
    private int pendingClaims;
}