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
public class PaymentReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @Column(nullable = false)
    private boolean sent = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public PaymentReminder(User user, LocalDate scheduleDate, boolean sent) {
        this.user = user;
        this.scheduleDate = scheduleDate;
        this.sent = sent;
    }
}