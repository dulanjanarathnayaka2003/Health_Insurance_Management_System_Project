package com.sliit.healthins.pattern.factory;

import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.dto.PaymentReminderDTO;
import com.sliit.healthins.model.*;

/**
 * Factory Pattern: Creates DTOs from domain objects
 * This centralizes DTO creation logic and makes it easier to maintain
 */
public interface DTOFactory {
    
    /**
     * Creates CustomerDTO from a User entity
     * @param user User entity
     * @return CustomerDTO
     */
    CustomerDTO createCustomerDTO(User user);
    
    /**
     * Creates InquiryDTO from Inquiry entity
     * @param inquiry Inquiry entity
     * @return InquiryDTO
     */
    InquiryDTO createInquiryDTO(Inquiry inquiry);
    
    /**
     * Creates PaymentReminderDTO from a Payment entity
     * @param payment Payment entity
     * @return PaymentReminderDTO
     */
    PaymentReminderDTO createPaymentReminderDTO(Payment payment);
}
