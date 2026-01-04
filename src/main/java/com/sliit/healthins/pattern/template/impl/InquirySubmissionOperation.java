package com.sliit.healthins.pattern.template.impl;

import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.template.CustomerPortalOperation;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.CustomerPortalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Template Method implementation for Inquiry Submission operation
 * Follows the standardized workflow for customer portal operations
 */
@Component
public class InquirySubmissionOperation extends CustomerPortalOperation<InquiryDTO, InquiryDTO> {
    
    private static final Logger logger = LoggerFactory.getLogger(InquirySubmissionOperation.class);
    
    private final UserRepository userRepository;
    private final CustomerPortalService customerPortalService;
    
    @Autowired
    public InquirySubmissionOperation(UserRepository userRepository,
                                     CustomerPortalService customerPortalService) {
        this.userRepository = userRepository;
        this.customerPortalService = customerPortalService;
    }
    
    @Override
    protected String getOperationName() {
        return "INQUIRY_SUBMISSION";
    }
    
    @Override
    protected void validateUser(Long userId) {
        logger.debug("Validating user: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!user.isActive()) {
            throw new IllegalStateException("User account is not active");
        }
    }
    
    @Override
    protected void validateRequest(InquiryDTO request) {
        logger.debug("Validating inquiry submission request");
        
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Inquiry type is required");
        }
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Inquiry title is required");
        }
        
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Inquiry description is required");
        }
        
        if (request.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Inquiry description cannot exceed 1000 characters");
        }
    }
    
    @Override
    protected void checkPermissions(Long userId, InquiryDTO request) {
        logger.debug("Checking permissions for inquiry submission");
        // Basic permission check - all active users can submit inquiries
        // Additional business rules can be added here if needed
    }
    
    @Override
    protected InquiryDTO executeBusinessLogic(InquiryDTO request, Long userId) {
        logger.debug("Executing inquiry submission business logic");
        return customerPortalService.submitInquiry(request, userId);
    }
    
    @Override
    protected boolean shouldSendNotification() {
        return false; // Inquiries typically don't need immediate notifications
    }
    
    @Override
    protected void handleError(Exception error, Long userId, InquiryDTO request) {
        super.handleError(error, userId, request);
        logger.error("Inquiry submission failed - Type: {}, Title: {}, User: {}", 
                    request.getType(), request.getTitle(), userId);
    }
}