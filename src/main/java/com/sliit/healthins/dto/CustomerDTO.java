package com.sliit.healthins.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String username;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String role;
    private String status;
    private String policyNumber;
    private boolean isActive;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setIsActive(boolean active) {
        this.isActive = active;

    }
}