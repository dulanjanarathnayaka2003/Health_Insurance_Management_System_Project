package com.sliit.healthins.pattern.observer.impl;

import com.sliit.healthins.pattern.observer.CustomerSupportEvent;
import com.sliit.healthins.pattern.observer.CustomerSupportObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Metrics Collector
 * Collects metrics about customer support operations
 */
@Component
public class MetricsCollectorObserver implements CustomerSupportObserver {
    
    private static final Logger metricsLogger = LoggerFactory.getLogger("METRICS");
    
    @Override
    public void onCustomerSupportEvent(CustomerSupportEvent event) {
        // Log metrics for monitoring and analytics
        metricsLogger.info("METRICS: {} operation performed by user: {} at {}", 
                          event.getEventType(), 
                          event.getUserId(), 
                          event.getTimestamp());
        
        // Here you could integrate with metrics systems like Micrometer, Prometheus, etc.
        // For example:
        // meterRegistry.counter("customer.support.operation", "type", event.getEventType()).increment();
    }
    
    @Override
    public String getObserverName() {
        return "MetricsCollector";
    }
}
