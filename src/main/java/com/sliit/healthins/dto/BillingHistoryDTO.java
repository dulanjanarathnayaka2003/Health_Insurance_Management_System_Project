package com.sliit.healthins.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class BillingHistoryDTO {
    private LocalDate date;
    private Double amount;
    private String status;
    private String description;
    private String type;

}
