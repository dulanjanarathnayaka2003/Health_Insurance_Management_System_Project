package com.sliit.healthins.dto;

import java.math.BigDecimal;

public class PolicyInfoDTO {
    private Long id;
    private String coverageType;
    private String description;
    private String benefits;
    private String coverageLimit;
    private BigDecimal price;

    public PolicyInfoDTO() {
    }

    public PolicyInfoDTO(Long id, String coverageType, String description, String benefits, String coverageLimit, BigDecimal price) {
        this.id = id;
        this.coverageType = coverageType;
        this.description = description;
        this.benefits = benefits;
        this.coverageLimit = coverageLimit;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getCoverageLimit() {
        return coverageLimit;
    }

    public void setCoverageLimit(String coverageLimit) {
        this.coverageLimit = coverageLimit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

