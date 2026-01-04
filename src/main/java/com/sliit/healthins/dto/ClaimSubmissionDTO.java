package com.sliit.healthins.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimSubmissionDTO {
    private Long policyId;
    private Long userId;
    private String policyNumber;
    private Double amount;
    private LocalDate claimDate;
    private LocalDate date;
    private String description;
    private String documentPath;
    private String email;
    private String notes;

    public ClaimSubmissionDTO() {}

    // Getters
    public Long getPolicyId() { return policyId; }
    public Long getUserId() { return userId; }
    public String getPolicyNumber() { return policyNumber; }
    public Double getAmount() { return amount; }
    public LocalDate getClaimDate() { return claimDate; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getDocumentPath() { return documentPath; }
    public String getEmail() { return email; }
    public String getNotes() { return notes; }

    // Setters
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public void setEmail(String email) { this.email = email; }
    public void setNotes(String notes) { this.notes = notes; }
}