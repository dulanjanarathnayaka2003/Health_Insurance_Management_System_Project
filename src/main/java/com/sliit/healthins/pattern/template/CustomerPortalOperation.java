package com.sliit.healthins.pattern.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template Method Pattern for Customer Portal Operations
 * Defines the skeleton of customer portal operations workflow
 */
public abstract class CustomerPortalOperation<T, R> {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerPortalOperation.class);
    
    /**
     * Template method defining the workflow for customer portal operations
     * @param request Operation request
     * @param userId User identifier
     * @return Operation result
     */
    public final R execute(T request, Long userId) {
        logger.info("Starting customer portal operation: {}", getOperationName());
        
        try {
            // Step 1: Validate user
            validateUser(userId);
            
            // Step 2: Validate request
            validateRequest(request);
            
            // Step 3: Check permissions
            checkPermissions(userId, request);
            
            // Step 4: Execute business logic
            R result = executeBusinessLogic(request, userId);
            
            // Step 5: Log operation
            logOperation(userId, request, result);
            
            // Step 6: Send notifications (optional)
            if (shouldSendNotification()) {
                sendNotification(userId, result);
            }
            
            logger.info("Customer portal operation completed successfully: {}", getOperationName());
            return result;
            
        } catch (Exception e) {
            logger.error("Customer portal operation failed: {}", getOperationName(), e);
            handleError(e, userId, request);
            throw e;
        }
    }
    
    /**
     * Get operation name for logging
     * @return Operation name
     */
    protected abstract String getOperationName();
    
    /**
     * Validate user exists and is active
     * @param userId User identifier
     */
    protected abstract void validateUser(Long userId);
    
    /**
     * Validate request data
     * @param request Operation request
     */
    protected abstract void validateRequest(T request);
    
    /**
     * Check user permissions for this operation
     * @param userId User identifier
     * @param request Operation request
     */
    protected abstract void checkPermissions(Long userId, T request);
    
    /**
     * Execute the main business logic
     * @param request Operation request
     * @param userId User identifier
     * @return Operation result
     */
    protected abstract R executeBusinessLogic(T request, Long userId);
    
    /**
     * Log the operation for audit purposes
     * @param userId User identifier
     * @param request Operation request
     * @param result Operation result
     */
    protected void logOperation(Long userId, T request, R result) {
        logger.info("Operation logged - User: {}, Operation: {}", userId, getOperationName());
    }
    
    /**
     * Determine if notification should be sent
     * @return true if notification should be sent
     */
    protected boolean shouldSendNotification() {
        return false; // Default: no notification
    }
    
    /**
     * Send notification to user
     * @param userId User identifier
     * @param result Operation result
     */
    protected void sendNotification(Long userId, R result) {
        logger.info("Notification sent for operation: {}", getOperationName());
    }
    
    /**
     * Handle operation errors
     * @param error Exception that occurred
     * @param userId User identifier
     * @param request Operation request
     */
    protected void handleError(Exception error, Long userId, T request) {
        logger.error("Error handling for operation: {} - User: {}", getOperationName(), userId);
    }
}