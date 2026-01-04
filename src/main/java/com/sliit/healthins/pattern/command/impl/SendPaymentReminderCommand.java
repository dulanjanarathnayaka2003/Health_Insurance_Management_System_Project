package com.sliit.healthins.pattern.command.impl;

import com.sliit.healthins.dto.PaymentReminderDTO;
import com.sliit.healthins.model.Payment;
import com.sliit.healthins.pattern.command.CustomerSupportCommand;
import com.sliit.healthins.repository.PaymentRepository;
import com.sliit.healthins.util.EmailSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Command: Send Payment Reminder Command
 * Implements the command pattern for payment reminder operations
 */
@Component
public class SendPaymentReminderCommand implements CustomerSupportCommand {
    
    private final PaymentRepository paymentRepository;
    private final EmailSenderUtil emailSenderUtil;
    
    private PaymentReminderDTO reminderDTO;
    
    @Autowired
    public SendPaymentReminderCommand(PaymentRepository paymentRepository, EmailSenderUtil emailSenderUtil) {
        this.paymentRepository = paymentRepository;
        this.emailSenderUtil = emailSenderUtil;
    }
    
    public SendPaymentReminderCommand setParameters(PaymentReminderDTO reminderDTO) {
        this.reminderDTO = reminderDTO;
        return this;
    }
    
    @Override
    public Object execute() {
        Payment payment = paymentRepository.findById(reminderDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + reminderDTO.getPaymentId()));
        
        String subject = "Payment Reminder - " + reminderDTO.getReminderType();
        String message = buildReminderMessage(payment, reminderDTO);
        
        emailSenderUtil.sendEmail(payment.getCustomer().getEmail(), subject, message);
        
        return "Payment reminder sent successfully to " + payment.getCustomer().getEmail();
    }
    
    private String buildReminderMessage(Payment payment, PaymentReminderDTO reminderDTO) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(payment.getCustomer().getName()).append(",\n\n");
        message.append("This is a ").append(reminderDTO.getReminderType().toLowerCase())
               .append(" reminder for your payment due.\n");
        message.append("Amount: ").append(payment.getAmount())
               .append(", Due Date: ").append(payment.getDueDate()).append(".\n\n");
        
        if (reminderDTO.getMessage() != null && !reminderDTO.getMessage().trim().isEmpty()) {
            message.append("Additional Message: ").append(reminderDTO.getMessage()).append("\n\n");
        }
        
        message.append("Please settle at your earliest convenience.\n\n");
        message.append("Regards,\nHealthInsure Team");
        
        return message.toString();
    }
    
    @Override
    public String getDescription() {
        return "Send payment reminder for payment ID: " + reminderDTO.getPaymentId();
    }
    
    @Override
    public boolean supportsUndo() {
        return false; // Cannot undo email sending
    }
}
