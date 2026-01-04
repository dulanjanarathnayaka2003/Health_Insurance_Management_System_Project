package com.sliit.healthins.pattern.strategy;

import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.User;
import java.util.List;
import java.util.Map;

/**
 * Strategy interface for different marketing campaign execution approaches
 */
public interface MarketingCampaignStrategy {
    
    /**
     * Execute the marketing campaign using specific strategy
     * @param campaign The campaign to execute
     * @param customers List of target customers
     * @return Execution results with metrics
     */
    Map<String, Object> executeCampaign(Campaign campaign, List<User> customers);
    
    /**
     * Get strategy name for identification
     * @return Strategy name
     */
    String getStrategyName();
    
    /**
     * Validate if campaign is suitable for this strategy
     * @param campaign Campaign to validate
     * @return true if suitable, false otherwise
     */
    boolean isApplicable(Campaign campaign);
}