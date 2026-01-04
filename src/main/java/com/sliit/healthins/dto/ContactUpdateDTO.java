package com.sliit.healthins.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContactUpdateDTO {
    private String email;
    private String phone;
    private String status;
    private String notes;

    public ContactUpdateDTO(String mail, Object o) {

    }
}