package com.sliit.healthins.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class CampaignDTO {
    private Long id;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String targetSegment;
    private String description;

    public CampaignDTO() {}

    public CampaignDTO(Long id, String name, String type, LocalDate startDate, LocalDate endDate, String status, String targetSegment, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.targetSegment = targetSegment;
        this.description = description;
    }
}
