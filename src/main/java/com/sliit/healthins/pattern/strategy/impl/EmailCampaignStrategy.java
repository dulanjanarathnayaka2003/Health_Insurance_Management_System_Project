package com.sliit.healthins.pattern.strategy.impl;

import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CampaignType;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.strategy.MarketingCampaignStrategy;
import com.sliit.healthins.util.EmailSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Email-specific marketing campaign execution strategy
 */
@Component
public class EmailCampaignStrategy implements MarketingCampaignStrategy {
    
    private final EmailSenderUtil emailUtil;
    
    @Autowired
    public EmailCampaignStrategy(EmailSenderUtil emailUtil) {
        this.emailUtil = emailUtil;
    }
    
    @Override
    public Map<String, Object> executeCampaign(Campaign campaign, List<User> customers) {
        int successCount = 0;
        int failureCount = 0;
        
        String subject = "Health Insurance: " + campaign.getName();
        String message = campaign.getDescription() != null ? campaign.getDescription() : 
                        "Thank you for being our valued customer!";
        
        for (User customer : customers) {
            try {
                emailUtil.sendEmail(customer.getEmail(), subject, message);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                System.err.println("Email failed for " + customer.getEmail() + ": " + e.getMessage());
            }
        }
        
        Map<String, Object> results = new HashMap<>();
        results.put("strategy", getStrategyName());
        results.put("sentCount", successCount);
        results.put("failedCount", failureCount);
        results.put("totalTargeted", customers.size());
        results.put("successRate", customers.size() > 0 ? (double) successCount / customers.size() * 100 : 0);
        
        return results;
    }
    
    @Override
    public String getStrategyName() {
        return "EMAIL_CAMPAIGN";
    }
    
    @Override
    public boolean isApplicable(Campaign campaign) {
        return campaign.getType() == CampaignType.EMAIL;
    }
}