package com.sliit.healthins.pattern.strategy.impl;

import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CampaignType;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.strategy.MarketingCampaignStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Social Media marketing campaign execution strategy
 */
@Component
public class SocialMediaCampaignStrategy implements MarketingCampaignStrategy {
    
    @Override
    public Map<String, Object> executeCampaign(Campaign campaign, List<User> customers) {
        // Simulate social media campaign execution
        int engagementCount = (int) (customers.size() * 0.15); // 15% engagement rate
        int reachCount = (int) (customers.size() * 2.5); // Extended reach through shares
        
        Map<String, Object> results = new HashMap<>();
        results.put("strategy", getStrategyName());
        results.put("targetedCustomers", customers.size());
        results.put("estimatedReach", reachCount);
        results.put("expectedEngagement", engagementCount);
        results.put("campaignPosted", true);
        results.put("platform", "Facebook, Instagram, Twitter");
        
        // Log campaign execution
        System.out.println("Social Media Campaign '" + campaign.getName() + "' posted across platforms");
        System.out.println("Expected reach: " + reachCount + " users");
        
        return results;
    }
    
    @Override
    public String getStrategyName() {
        return "SOCIAL_MEDIA_CAMPAIGN";
    }
    
    @Override
    public boolean isApplicable(Campaign campaign) {
        return campaign.getType() == CampaignType.SOCIAL_MEDIA;
    }
}