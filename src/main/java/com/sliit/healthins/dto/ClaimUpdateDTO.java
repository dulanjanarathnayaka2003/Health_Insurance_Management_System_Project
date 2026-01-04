package com.sliit.healthins.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class ClaimUpdateDTO {
    private Long id;
    private String status;
    private String notes;

    public ClaimUpdateDTO(String approved) {
        this.status = approved;
    }
}