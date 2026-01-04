package com.sliit.healthins.pattern.config;

import com.sliit.healthins.pattern.observer.MarketingEventObserver;
import com.sliit.healthins.pattern.strategy.MarketingCampaignStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for marketing design patterns
 */
@Configuration
public class MarketingPatternConfiguration {
    
    @Autowired
    private List<MarketingCampaignStrategy> strategies;
    
    @Autowired
    private List<MarketingEventObserver> observers;
    
    /**
     * Bean to verify pattern implementations are loaded
     */
    @Bean
    public String marketingPatternsInfo() {
        System.out.println("=== Marketing Design Patterns Loaded ===");
        System.out.println("Strategies: " + strategies.size());
        strategies.forEach(s -> System.out.println("  - " + s.getStrategyName()));
        
        System.out.println("Observers: " + observers.size());
        observers.forEach(o -> System.out.println("  - " + o.getObserverName()));
        System.out.println("========================================");
        
        return "Marketing patterns configured successfully";
    }
}