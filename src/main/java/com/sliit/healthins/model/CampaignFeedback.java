package com.sliit.healthins.model;

import jakarta.persistence.*;

@Entity
public class CampaignFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    private Long userId;
    private String feedbackText;
    private int rating;
    
    public CampaignFeedback() {}

    public CampaignFeedback(Campaign campaign, Long userId, String feedbackText, int rating) {
        this.campaign = campaign;
        this.userId = userId;
        this.feedbackText = feedbackText;
        this.rating = rating;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
