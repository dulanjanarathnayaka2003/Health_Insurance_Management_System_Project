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
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false, precision = 10)
    private Double salary;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column
    private Integer performanceRating;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Employee(User user, String department, Double salary, LocalDate hireDate, Integer performanceRating) {
        this.user = user;
        this.department = department;
        this.salary = salary;
        this.hireDate = hireDate;
        this.performanceRating = performanceRating;
    }

    public void setUserId(long l) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(l);
    }

    public Object getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

}