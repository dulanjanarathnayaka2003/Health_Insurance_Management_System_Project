package com.sliit.healthins.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private String name;
    private String username;
    private String email;
    private String phone;
    private boolean isActive;
    private String createdAt;
    private int totalPolicies;
    private int totalClaims;
    private int pendingClaims;
    
    // Bank account information
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String branch;
}
