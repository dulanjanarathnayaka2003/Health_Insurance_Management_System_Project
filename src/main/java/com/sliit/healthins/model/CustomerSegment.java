package com.sliit.healthins.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class CustomerSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String criteria;

    @ElementCollection
    @CollectionTable(name = "customer_segment_customers", joinColumns = @JoinColumn(name = "segment_id"))
    @Column(name = "customer_id")
    private List<Long> customerIds;

    public CustomerSegment(String criteria, List<Long> customerIds) {
        this.criteria = criteria;
        this.customerIds = customerIds;
    }
}