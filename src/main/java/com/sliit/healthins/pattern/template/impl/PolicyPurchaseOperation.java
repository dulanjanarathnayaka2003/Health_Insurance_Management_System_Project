package com.sliit.healthins.pattern.template.impl;

import com.sliit.healthins.dto.PolicyDetailsDTO;
import com.sliit.healthins.dto.PolicyPurchaseDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.PolicyInfo;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.template.CustomerPortalOperation;
import com.sliit.healthins.repository.PolicyInfoRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.CustomerPortalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Template Method implementation for Policy Purchase operation
 * Follows the standardized workflow for customer portal operations
 */
@Component
public class PolicyPurchaseOperation extends CustomerPortalOperation<PolicyPurchaseDTO, PolicyDetailsDTO> {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyPurchaseOperation.class);
    
    private final UserRepository userRepository;
    private final PolicyInfoRepository policyInfoRepository;
    private final CustomerPortalService customerPortalService;
    
    @Autowired
    public PolicyPurchaseOperation(UserRepository userRepository,
                                  PolicyInfoRepository policyInfoRepository,
                                  CustomerPortalService customerPortalService) {
        this.userRepository = userRepository;
        this.policyInfoRepository = policyInfoRepository;
        this.customerPortalService = customerPortalService;
    }
    
    @Override
    protected String getOperationName() {
        return "POLICY_PURCHASE";
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
    protected void validateRequest(PolicyPurchaseDTO request) {
        logger.debug("Validating policy purchase request");
        
        if (request.getPolicyInfoId() == null) {
            throw new IllegalArgumentException("Policy Info ID is required");
        }
        
        if (request.getCardNumber() == null || request.getCardNumber().length() < 13) {
            throw new IllegalArgumentException("Valid card number is required");
        }
        
        if (request.getExpiryDate() == null || request.getExpiryDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Card expiry date is required");
        }
        
        if (request.getCvv() == null || request.getCvv().length() < 3) {
            throw new IllegalArgumentException("Valid CVV is required");
        }
    }
    
    @Override
    protected void checkPermissions(Long userId, PolicyPurchaseDTO request) {
        logger.debug("Checking permissions for policy purchase");
        
        PolicyInfo policyInfo = policyInfoRepository.findById(request.getPolicyInfoId())
            .orElseThrow(() -> new ResourceNotFoundException("Policy Info not found"));
        
        // Additional business rules can be added here
        if (policyInfo.getPrice() == null || policyInfo.getPrice().doubleValue() <= 0) {
            throw new IllegalStateException("Invalid policy pricing");
        }
    }
    
    @Override
    protected PolicyDetailsDTO executeBusinessLogic(PolicyPurchaseDTO request, Long userId) {
        logger.debug("Executing policy purchase business logic");
        return customerPortalService.purchasePolicy(userId, request);
    }
    
    @Override
    protected boolean shouldSendNotification() {
        return true; // Policy purchases should trigger notifications
    }
    
    @Override
    protected void sendNotification(Long userId, PolicyDetailsDTO result) {
        logger.info("Policy purchase notification sent - Policy: {}, User: {}", 
                   result.getPolicyNumber(), userId);
        // Notification logic would be implemented here
    }
    
    @Override
    protected void handleError(Exception error, Long userId, PolicyPurchaseDTO request) {
        super.handleError(error, userId, request);
        logger.error("Policy purchase failed - Policy Info: {}, User: {}", 
                    request.getPolicyInfoId(), userId);
    }
}