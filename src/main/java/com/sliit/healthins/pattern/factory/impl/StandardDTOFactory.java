package com.sliit.healthins.pattern.factory.impl;

import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.dto.PaymentReminderDTO;
import com.sliit.healthins.model.*;
import com.sliit.healthins.pattern.factory.DTOFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Factory: Standard DTO Factory Implementation
 * Creates DTOs using ModelMapper with custom mapping logic
 */
@Component
public class StandardDTOFactory implements DTOFactory {
    
    private final ModelMapper modelMapper;
    
    @Autowired
    public StandardDTOFactory(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
    @Override
    public CustomerDTO createCustomerDTO(User user) {
        CustomerDTO dto = modelMapper.map(user, CustomerDTO.class);
        
        // Custom mapping logic
        dto.setStatus(user.getPolicies() != null && !user.getPolicies().isEmpty() ? 
                     user.getPolicies().getFirst().getStatus().name() : "N/A");
        dto.setPolicyNumber(user.getPolicies() != null && !user.getPolicies().isEmpty() ? 
                           user.getPolicies().getFirst().getPolicyNumber() : "N/A");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsActive(user.isActive());
        
        return dto;
    }
    
    @Override
    public InquiryDTO createInquiryDTO(Inquiry inquiry) {
        InquiryDTO dto = new InquiryDTO(
                inquiry.getId(),
                inquiry.getCustomer() != null ? inquiry.getCustomer().getId() : null,
                inquiry.getType(),
                inquiry.getDescription(),
                inquiry.getStatus().name(),
                inquiry.getResolutionDate()
        );
        
        dto.setTitle(inquiry.getTitle());
        dto.setCreatedAt(inquiry.getCreatedAt());
        dto.setCustomerName(inquiry.getCustomer() != null ? inquiry.getCustomer().getName() : "Unknown");
        // Note: Inquiry entity doesn't have response field, using title as response for now
        dto.setResponse(inquiry.getTitle());
        
        return dto;
    }
    
    @Override
    public PaymentReminderDTO createPaymentReminderDTO(Payment payment) {
        PaymentReminderDTO dto = new PaymentReminderDTO();
        dto.setPaymentId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setDueDate(payment.getDueDate());
        dto.setPolicyNumber(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : "N/A");
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "N/A");
        
        return dto;
    }
}
