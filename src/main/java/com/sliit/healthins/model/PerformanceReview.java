package com.sliit.healthins.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PerformanceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private Integer rating;

    private String comments;

    private LocalDate date;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public PerformanceReview(LocalDate date, String comments, Integer rating, Employee employee) {
        this.date = date;
        this.comments = comments;
        this.rating = rating;
        this.employee = employee;
    }
}