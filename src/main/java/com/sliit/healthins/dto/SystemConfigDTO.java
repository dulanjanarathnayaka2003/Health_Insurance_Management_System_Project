package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO {
    private String siteName;
    private String siteEmail;
    private String timezone;
    private boolean maintenanceMode;
}

