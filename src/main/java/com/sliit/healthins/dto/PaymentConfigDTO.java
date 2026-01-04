package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfigDTO {
    private String paymentGateway;
    private String merchantId;
    private String publicKey;
    private String privateKey;
    private boolean testMode;
}

