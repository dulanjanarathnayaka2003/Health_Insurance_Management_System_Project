package com.sliit.healthins.pattern.observer.impl;

import com.sliit.healthins.pattern.observer.MarketingEvent;
import com.sliit.healthins.pattern.observer.MarketingEventObserver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Observer for collecting marketing analytics and metrics
 */
@Component
public class MarketingAnalyticsObserver implements MarketingEventObserver {
    
    private static final List<String> HANDLED_EVENTS = Arrays.asList(
        "CAMPAIGN_CREATED", "CAMPAIGN_EXECUTED", "EMAIL_SENT", "CAMPAIGN_COMPLETED"
    );
    
    @Override
    public void onMarketingEvent(MarketingEvent event) {
        switch (event.getEventType()) {
            case "CAMPAIGN_CREATED":
                trackCampaignCreation(event);
                break;
            case "CAMPAIGN_EXECUTED":
                trackCampaignExecution(event);
                break;
            case "EMAIL_SENT":
                trackEmailMetrics(event);
                break;
            case "CAMPAIGN_COMPLETED":
                trackCampaignCompletion(event);
                break;
        }
    }
    
    private void trackCampaignCreation(MarketingEvent event) {
        System.out.println("[ANALYTICS] Campaign Created: " + event.getCampaign().getName());
        System.out.println("[ANALYTICS] Campaign Type: " + event.getCampaign().getType());
        System.out.println("[ANALYTICS] Created by: " + event.getUserId());
        
        // In real implementation, this would save to analytics database
        logMetric("campaign.created", 1, event.getCampaign().getType().toString());
    }
    
    private void trackCampaignExecution(MarketingEvent event) {
        Map<String, Object> data = event.getEventData();
        System.out.println("[ANALYTICS] Campaign Executed: " + event.getCampaign().getName());
        System.out.println("[ANALYTICS] Strategy: " + data.get("strategy"));
        System.out.println("[ANALYTICS] Target Count: " + data.get("totalTargeted"));
        
        logMetric("campaign.executed", 1, data.get("strategy").toString());
        if (data.containsKey("sentCount")) {
            logMetric("emails.sent", (Integer) data.get("sentCount"), "total");
        }
    }
    
    private void trackEmailMetrics(MarketingEvent event) {
        Map<String, Object> data = event.getEventData();
        System.out.println("[ANALYTICS] Email Metrics - Success: " + data.get("successCount") + 
                          ", Failed: " + data.get("failureCount"));
        
        logMetric("email.success", (Integer) data.get("successCount"), "count");
        logMetric("email.failure", (Integer) data.get("failureCount"), "count");
    }
    
    private void trackCampaignCompletion(MarketingEvent event) {
        System.out.println("[ANALYTICS] Campaign Completed: " + event.getCampaign().getName());
        System.out.println("[ANALYTICS] Duration: " + event.getCampaign().getStartDate() + 
                          " to " + event.getCampaign().getEndDate());
        
        logMetric("campaign.completed", 1, event.getCampaign().getType().toString());
    }
    
    private void logMetric(String metricName, Object value, String category) {
        // Simulate metrics logging - in real implementation would use metrics system
        System.out.println("[METRICS] " + metricName + "=" + value + " category=" + category + 
                          " timestamp=" + System.currentTimeMillis());
    }
    
    @Override
    public String getObserverName() {
        return "MarketingAnalyticsObserver";
    }
    
    @Override
    public boolean shouldHandle(String eventType) {
        return HANDLED_EVENTS.contains(eventType);
    }
}