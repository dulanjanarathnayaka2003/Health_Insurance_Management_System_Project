package com.sliit.healthins.pattern.facade;

import com.sliit.healthins.dto.CampaignDTO;
import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.observer.MarketingEvent;
import com.sliit.healthins.pattern.observer.MarketingEventObserver;
import com.sliit.healthins.pattern.strategy.MarketingCampaignStrategy;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade that integrates Strategy and Observer patterns for marketing operations
 */
@Component
public class MarketingFacade {
    
    private final MarketingService marketingService;
    private final UserRepository userRepository;
    private final List<MarketingCampaignStrategy> strategies;
    private final List<MarketingEventObserver> observers;
    
    @Autowired
    public MarketingFacade(MarketingService marketingService, 
                          UserRepository userRepository,
                          List<MarketingCampaignStrategy> strategies,
                          List<MarketingEventObserver> observers) {
        this.marketingService = marketingService;
        this.userRepository = userRepository;
        this.strategies = strategies;
        this.observers = observers;
    }
    
    /**
     * Execute campaign using appropriate strategy and notify observers
     */
    public Map<String, Object> executeCampaignWithStrategy(Long campaignId, String userId) {
        Campaign campaign = marketingService.getCampaignById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        // Find appropriate strategy
        MarketingCampaignStrategy strategy = strategies.stream()
                .filter(s -> s.isApplicable(campaign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No strategy found for campaign type: " + campaign.getType()));
        
        // Get target customers
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        
        // Execute campaign
        Map<String, Object> results = strategy.executeCampaign(campaign, customers);
        
        // Notify observers
        notifyObservers("CAMPAIGN_EXECUTED", campaign, results, userId);
        
        return results;
    }
    
    /**
     * Create campaign and notify observers
     */
    public Campaign createCampaignWithNotification(CampaignDTO dto, String userId) {
        Campaign campaign = marketingService.createCampaign(dto);
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("campaignType", dto.getType());
        eventData.put("targetSegment", dto.getTargetSegment());
        
        notifyObservers("CAMPAIGN_CREATED", campaign, eventData, userId);
        
        return campaign;
    }
    
    /**
     * Send emails with strategy pattern and observer notifications
     */
    public Map<String, Object> sendEmailsWithTracking(Long campaignId, String userId) {
        Campaign campaign = marketingService.getCampaignById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        // Use email strategy specifically
        MarketingCampaignStrategy emailStrategy = strategies.stream()
                .filter(s -> s.getStrategyName().equals("EMAIL_CAMPAIGN"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Email strategy not available"));
        
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        Map<String, Object> results = emailStrategy.executeCampaign(campaign, customers);
        
        // Notify observers about email sending
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("successCount", results.get("sentCount"));
        emailData.put("failureCount", results.get("failedCount"));
        emailData.put("totalCustomers", customers.size());
        
        notifyObservers("EMAIL_SENT", campaign, emailData, userId);
        
        return results;
    }
    
    /**
     * Get available strategies
     */
    public List<String> getAvailableStrategies() {
        return strategies.stream()
                .map(MarketingCampaignStrategy::getStrategyName)
                .toList();
    }
    
    /**
     * Notify all relevant observers about an event
     */
    private void notifyObservers(String eventType, Campaign campaign, Map<String, Object> eventData, String userId) {
        MarketingEvent event = new MarketingEvent(eventType, campaign, eventData, userId);
        
        observers.stream()
                .filter(observer -> observer.shouldHandle(eventType))
                .forEach(observer -> {
                    try {
                        observer.onMarketingEvent(event);
                    } catch (Exception e) {
                        System.err.println("Observer " + observer.getObserverName() + " failed: " + e.getMessage());
                    }
                });
    }
}