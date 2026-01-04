package com.sliit.healthins.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ReviewDTO {
    private Long employeeId;
    private Integer rating;
    private String comments;
    private LocalDate date;

    public Object getReviewDate() {
        return date;
    }
}