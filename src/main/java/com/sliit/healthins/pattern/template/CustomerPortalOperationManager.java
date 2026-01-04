package com.sliit.healthins.pattern.template;

import com.sliit.healthins.dto.ClaimSubmissionDTO;
import com.sliit.healthins.dto.InquiryDTO;
import com.sliit.healthins.dto.PolicyDetailsDTO;
import com.sliit.healthins.dto.PolicyPurchaseDTO;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.pattern.template.impl.ClaimSubmissionOperation;
import com.sliit.healthins.pattern.template.impl.InquirySubmissionOperation;
import com.sliit.healthins.pattern.template.impl.PolicyPurchaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manager service that coordinates template method operations
 * Demonstrates how the Template Method pattern integrates with existing services
 */
@Service
public class CustomerPortalOperationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerPortalOperationManager.class);
    
    private final ClaimSubmissionOperation claimSubmissionOperation;
    private final PolicyPurchaseOperation policyPurchaseOperation;
    private final InquirySubmissionOperation inquirySubmissionOperation;
    
    @Autowired
    public CustomerPortalOperationManager(ClaimSubmissionOperation claimSubmissionOperation,
                                         PolicyPurchaseOperation policyPurchaseOperation,
                                         InquirySubmissionOperation inquirySubmissionOperation) {
        this.claimSubmissionOperation = claimSubmissionOperation;
        this.policyPurchaseOperation = policyPurchaseOperation;
        this.inquirySubmissionOperation = inquirySubmissionOperation;
    }
    
    /**
     * Execute claim submission using template method pattern
     * @param claimDTO Claim submission data
     * @param userId User identifier
     * @return Submitted claim
     */
    public Claim executeClaimSubmission(ClaimSubmissionDTO claimDTO, Long userId) {
        logger.info("Executing claim submission operation for user: {}", userId);
        return claimSubmissionOperation.execute(claimDTO, userId);
    }
    
    /**
     * Execute policy purchase using template method pattern
     * @param purchaseDTO Policy purchase data
     * @param userId User identifier
     * @return Purchased policy details
     */
    public PolicyDetailsDTO executePolicyPurchase(PolicyPurchaseDTO purchaseDTO, Long userId) {
        logger.info("Executing policy purchase operation for user: {}", userId);
        return policyPurchaseOperation.execute(purchaseDTO, userId);
    }
    
    /**
     * Execute inquiry submission using template method pattern
     * @param inquiryDTO Inquiry data
     * @param userId User identifier
     * @return Submitted inquiry
     */
    public InquiryDTO executeInquirySubmission(InquiryDTO inquiryDTO, Long userId) {
        logger.info("Executing inquiry submission operation for user: {}", userId);
        return inquirySubmissionOperation.execute(inquiryDTO, userId);
    }
}