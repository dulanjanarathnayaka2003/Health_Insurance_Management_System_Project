package com.sliit.healthins.pattern.decorator.impl;

import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.dto.PaymentReminderDTO;
import com.sliit.healthins.model.*;
import com.sliit.healthins.pattern.decorator.DTOFactoryDecorator;
import com.sliit.healthins.pattern.factory.DTOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete Decorator: Logging DTO Factory
 * Adds comprehensive logging to DTO creation operations
 */
public class LoggingDTOFactoryDecorator extends DTOFactoryDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingDTOFactoryDecorator.class);
    
    public LoggingDTOFactoryDecorator(DTOFactory wrappedFactory) {
        super(wrappedFactory);
    }
    
    @Override
    public CustomerDTO createCustomerDTO(User user) {
        logger.debug("Creating CustomerDTO for user ID: {}", user.getId());
        long startTime = System.currentTimeMillis();
        
        try {
            CustomerDTO result = wrappedFactory.createCustomerDTO(user);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("CustomerDTO created successfully for user ID: {} in {}ms", user.getId(), duration);
            return result;
        } catch (Exception e) {
            logger.error("Failed to create CustomerDTO for user ID: {}", user.getId(), e);
            throw e;
        }
    }
    
    @Override
    public InquiryDTO createInquiryDTO(Inquiry inquiry) {
        logger.debug("Creating InquiryDTO for inquiry ID: {}", inquiry.getId());
        long startTime = System.currentTimeMillis();
        
        try {
            InquiryDTO result = wrappedFactory.createInquiryDTO(inquiry);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("InquiryDTO created successfully for inquiry ID: {} in {}ms", inquiry.getId(), duration);
            return result;
        } catch (Exception e) {
            logger.error("Failed to create InquiryDTO for inquiry ID: {}", inquiry.getId(), e);
            throw e;
        }
    }
    
    @Override
    public PaymentReminderDTO createPaymentReminderDTO(Payment payment) {
        logger.debug("Creating PaymentReminderDTO for payment ID: {}", payment.getId());
        long startTime = System.currentTimeMillis();
        
        try {
            PaymentReminderDTO result = wrappedFactory.createPaymentReminderDTO(payment);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("PaymentReminderDTO created successfully for payment ID: {} in {}ms", payment.getId(), duration);
            return result;
        } catch (Exception e) {
            logger.error("Failed to create PaymentReminderDTO for payment ID: {}", payment.getId(), e);
            throw e;
        }
    }
}


