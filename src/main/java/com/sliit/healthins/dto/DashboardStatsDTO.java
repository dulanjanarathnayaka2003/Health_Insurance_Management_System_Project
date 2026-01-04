package com.sliit.healthins.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalCustomers;
    private long pendingClaims;
    private long resolvedInquiries;
    private long overduePayments;

}