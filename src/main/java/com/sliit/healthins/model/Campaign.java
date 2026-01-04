package com.sliit.healthins.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String targetSegment; // JSON or criteria string

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Campaign(String name, CampaignType type, LocalDate startDate, LocalDate endDate, CampaignStatus status, String targetSegment, String description) {
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.targetSegment = targetSegment;
        this.description = description;
    }

    public Campaign(String name, String type, LocalDate startDate, LocalDate endDate, String status, String targetSegment, String description) {
        this.name = name;
        this.type = CampaignType.valueOf(type.toUpperCase());
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = CampaignStatus.valueOf(status.toUpperCase());
        this.targetSegment = targetSegment;
        this.description = description;
    }
}
