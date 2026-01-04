package com.sliit.healthins.pattern.observer.impl;

import com.sliit.healthins.pattern.observer.MarketingEvent;
import com.sliit.healthins.pattern.observer.MarketingEventObserver;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Observer for auditing marketing activities
 */
@Component
public class MarketingAuditObserver implements MarketingEventObserver {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void onMarketingEvent(MarketingEvent event) {
        String auditLog = createAuditLog(event);
        
        // Log to console (in real implementation would save to audit database)
        System.out.println("[AUDIT] " + auditLog);
        
        // For critical events, could also send notifications
        if (isCriticalEvent(event.getEventType())) {
            System.out.println("[AUDIT-ALERT] Critical marketing event logged: " + event.getEventType());
        }
    }
    
    private String createAuditLog(MarketingEvent event) {
        StringBuilder log = new StringBuilder();
        log.append("Timestamp: ").append(event.getTimestamp().format(FORMATTER));
        log.append(" | Event: ").append(event.getEventType());
        log.append(" | User: ").append(event.getUserId());
        
        if (event.getCampaign() != null) {
            log.append(" | Campaign: ").append(event.getCampaign().getName());
            log.append(" (ID: ").append(event.getCampaign().getId()).append(")");
        }
        
        if (event.getEventData() != null && !event.getEventData().isEmpty()) {
            log.append(" | Data: ").append(event.getEventData().toString());
        }
        
        return log.toString();
    }
    
    private boolean isCriticalEvent(String eventType) {
        return eventType.equals("CAMPAIGN_EXECUTED") || 
               eventType.equals("BULK_EMAIL_SENT") ||
               eventType.equals("CAMPAIGN_DELETED");
    }
    
    @Override
    public String getObserverName() {
        return "MarketingAuditObserver";
    }
    
    @Override
    public boolean shouldHandle(String eventType) {
        // Audit observer handles all marketing events
        return true;
    }
}