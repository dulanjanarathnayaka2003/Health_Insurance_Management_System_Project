package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long claimId;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardholderName;
    private Double amount;
    private String status;
    private String paymentDate;
}
