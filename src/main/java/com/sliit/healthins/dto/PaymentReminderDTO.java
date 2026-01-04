package com.sliit.healthins.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PaymentReminderDTO {
    private Long id;
    private Long paymentId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String policyNumber;
    private String status;
    private String reminderType;
    private String message;
}