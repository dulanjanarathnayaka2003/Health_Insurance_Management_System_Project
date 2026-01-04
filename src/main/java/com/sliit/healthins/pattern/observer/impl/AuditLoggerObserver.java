package com.sliit.healthins.pattern.observer.impl;

import com.sliit.healthins.pattern.observer.CustomerSupportEvent;
import com.sliit.healthins.pattern.observer.CustomerSupportObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Audit Logger
 * Logs all customer support events for audit purposes
 */
@Component
public class AuditLoggerObserver implements CustomerSupportObserver {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    @Override
    public void onCustomerSupportEvent(CustomerSupportEvent event) {
        auditLogger.info("AUDIT: {} - {} by user: {} at {}", 
                        event.getEventType(), 
                        event.getDescription(), 
                        event.getUserId(), 
                        event.getTimestamp());
        
        // Log additional event data if present
        if (event.getEventData() != null && !event.getEventData().isEmpty()) {
            auditLogger.debug("Event data: {}", event.getEventData());
        }
    }
    
    @Override
    public String getObserverName() {
        return "AuditLogger";
    }
}
