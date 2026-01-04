package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityConfigDTO {
    private Integer sessionTimeout;
    private Integer passwordExpiry;
    private boolean twoFactorAuth;
    private boolean loginNotifications;
    private Integer maxLoginAttempts;
}

