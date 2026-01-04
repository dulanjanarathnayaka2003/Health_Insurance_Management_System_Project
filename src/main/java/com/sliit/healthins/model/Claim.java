package com.sliit.healthins.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_id", nullable = false, unique = true, length = 100)
    private String claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "document_path", nullable = true, length = 500)
    private String documentPath;

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnoreProperties({"claims", "payments", "customer"})
    private Policy policy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Getters
    public Long getId() { return id; }
    public String getClaimId() { return claimId; }
    public ClaimStatus getStatus() { return status; }
    public Double getAmount() { return amount; }
    public String getDocumentPath() { return documentPath; }
    public LocalDate getClaimDate() { return claimDate; }
    public String getNotes() { return notes; }
    public Policy getPolicy() { return policy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setClaimId(String claimId) { this.claimId = claimId; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setPolicyId(String pol123) {
        this.claimId = pol123;
    }

    public void setDescription(String medicalClaim) {
        this.notes = medicalClaim;
    }

    public String getPolicyId() {
        return this.claimId;
    }
}