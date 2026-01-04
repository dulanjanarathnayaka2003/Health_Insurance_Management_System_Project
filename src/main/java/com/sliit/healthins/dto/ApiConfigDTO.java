package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiConfigDTO {
    private Integer apiRateLimit;
    private Integer apiTimeout;
    private boolean apiLogging;
    private boolean corsEnabled;
}

