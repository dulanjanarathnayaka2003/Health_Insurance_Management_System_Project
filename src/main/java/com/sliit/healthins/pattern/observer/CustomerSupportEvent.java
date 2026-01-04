package com.sliit.healthins.pattern.observer;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event class for customer support operations
 * Contains information about the event that occurred
 */
public class CustomerSupportEvent {
    
    private final String eventType;
    private final String description;
    private final LocalDateTime timestamp;
    private final Map<String, Object> eventData;
    private final String userId;
    
    public CustomerSupportEvent(String eventType, String description, String userId, Map<String, Object> eventData) {
        this.eventType = eventType;
        this.description = description;
        this.userId = userId;
        this.eventData = eventData;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getEventData() {
        return eventData;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public String toString() {
        return String.format("CustomerSupportEvent{type='%s', description='%s', timestamp=%s, userId='%s'}", 
                           eventType, description, timestamp, userId);
    }
}
