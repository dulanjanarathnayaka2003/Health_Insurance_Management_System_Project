package com.sliit.healthins.pattern.decorator;

import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.dto.PaymentReminderDTO;
import com.sliit.healthins.pattern.factory.DTOFactory;
import com.sliit.healthins.model.*;

/**
 * Decorator Pattern: Base decorator for DTO Factory
 * Adds enhanced logging capabilities to DTO creation
 */
public abstract class DTOFactoryDecorator implements DTOFactory {
    
    protected final DTOFactory wrappedFactory;
    
    public DTOFactoryDecorator(DTOFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
    }
    
    @Override
    public CustomerDTO createCustomerDTO(User user) {
        return wrappedFactory.createCustomerDTO(user);
    }
    
    @Override
    public InquiryDTO createInquiryDTO(Inquiry inquiry) {
        return wrappedFactory.createInquiryDTO(inquiry);
    }
    
    @Override
    public PaymentReminderDTO createPaymentReminderDTO(Payment payment) {
        return wrappedFactory.createPaymentReminderDTO(payment);
    }
}


