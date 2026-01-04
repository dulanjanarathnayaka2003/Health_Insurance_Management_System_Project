package com.sliit.healthins.pattern.template.impl;

import com.sliit.healthins.dto.ClaimSubmissionDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.model.Policy;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.template.CustomerPortalOperation;
import com.sliit.healthins.repository.PolicyRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.CustomerPortalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Template Method implementation for Claim Submission operation
 * Follows the standardized workflow for customer portal operations
 */
@Component
public class ClaimSubmissionOperation extends CustomerPortalOperation<ClaimSubmissionDTO, Claim> {
    
    private static final Logger logger = LoggerFactory.getLogger(ClaimSubmissionOperation.class);
    
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final CustomerPortalService customerPortalService;
    
    @Autowired
    public ClaimSubmissionOperation(UserRepository userRepository, 
                                   PolicyRepository policyRepository,
                                   CustomerPortalService customerPortalService) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.customerPortalService = customerPortalService;
    }
    
    @Override
    protected String getOperationName() {
        return "CLAIM_SUBMISSION";
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
    protected void validateRequest(ClaimSubmissionDTO request) {
        logger.debug("Validating claim submission request");
        
        if (request.getPolicyId() == null) {
            throw new IllegalArgumentException("Policy ID is required");
        }
        
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Claim amount must be greater than zero");
        }
        
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Claim description is required");
        }
    }
    
    @Override
    protected void checkPermissions(Long userId, ClaimSubmissionDTO request) {
        logger.debug("Checking permissions for claim submission");
        
        Policy policy = policyRepository.findById(request.getPolicyId())
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        if (!policy.getCustomer().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to submit claim for this policy");
        }
        
        if (!"ACTIVE".equals(policy.getStatus())) {
            throw new IllegalStateException("Cannot submit claim for inactive policy");
        }
    }
    
    @Override
    protected Claim executeBusinessLogic(ClaimSubmissionDTO request, Long userId) {
        logger.debug("Executing claim submission business logic");
        return customerPortalService.submitClaim(request, userId);
    }
    
    @Override
    protected boolean shouldSendNotification() {
        return true; // Claims should trigger notifications
    }
    
    @Override
    protected void sendNotification(Long userId, Claim result) {
        logger.info("Claim submission notification sent - Claim ID: {}, User: {}", 
                   result.getClaimId(), userId);
        // Notification logic would be implemented here
    }
    
    @Override
    protected void handleError(Exception error, Long userId, ClaimSubmissionDTO request) {
        super.handleError(error, userId, request);
        logger.error("Claim submission failed - Policy: {}, Amount: {}, User: {}", 
                    request.getPolicyId(), request.getAmount(), userId);
    }
}