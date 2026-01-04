package com.sliit.healthins.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_info")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PolicyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String coverageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String benefits;

    @Column(nullable = false)
    private String coverageLimit;

    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public PolicyInfo(String coverageType, String description, String benefits, String coverageLimit) {
        this.coverageType = coverageType;
        this.description = description;
        this.benefits = benefits;
        this.coverageLimit = coverageLimit;
    }

    public PolicyInfo(String coverageType, String description, String benefits, String coverageLimit, java.math.BigDecimal price) {
        this.coverageType = coverageType;
        this.description = description;
        this.benefits = benefits;
        this.coverageLimit = coverageLimit;
        this.price = price;
    }
}

