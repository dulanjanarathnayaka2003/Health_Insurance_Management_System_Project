package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SegmentDTO {
    private String criteria;
    private List<Long> customerIds;

    public SegmentDTO(String criteria, List<Long> customerIds) {
        this.criteria = criteria;
        this.customerIds = customerIds;
    }
}