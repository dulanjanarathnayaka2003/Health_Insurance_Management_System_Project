package com.sliit.healthins.pattern.observer;

import com.sliit.healthins.model.Campaign;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Marketing event data structure for observer pattern
 */
public class MarketingEvent {
    
    private final String eventType;
    private final Campaign campaign;
    private final Map<String, Object> eventData;
    private final LocalDateTime timestamp;
    private final String userId;
    
    public MarketingEvent(String eventType, Campaign campaign, Map<String, Object> eventData, String userId) {
        this.eventType = eventType;
        this.campaign = campaign;
        this.eventData = eventData;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public Campaign getCampaign() {
        return campaign;
    }
    
    public Map<String, Object> getEventData() {
        return eventData;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public String toString() {
        return "MarketingEvent{" +
                "eventType='" + eventType + '\'' +
                ", campaignId=" + (campaign != null ? campaign.getId() : "null") +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }
}