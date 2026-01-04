package com.sliit.healthins.pattern.observer;

/**
 * Observer interface for marketing events
 */
public interface MarketingEventObserver {
    
    /**
     * Handle marketing event notification
     * @param event The marketing event that occurred
     */
    void onMarketingEvent(MarketingEvent event);
    
    /**
     * Get observer name for identification
     * @return Observer name
     */
    String getObserverName();
    
    /**
     * Check if observer should handle this event type
     * @param eventType The type of event
     * @return true if should handle, false otherwise
     */
    boolean shouldHandle(String eventType);
}