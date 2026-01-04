package com.sliit.healthins.pattern.observer;

/**
 * Observer Pattern: Defines the interface for observers that listen to customer support events
 * This allows for decoupled event handling and notifications
 */
public interface CustomerSupportObserver {
    
    /**
     * Called when a customer support event occurs
     * @param event The event that occurred
     */
    void onCustomerSupportEvent(CustomerSupportEvent event);
    
    /**
     * Gets the observer name for identification
     * @return Observer name
     */
    String getObserverName();
}
