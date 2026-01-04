package com.sliit.healthins.model;

import jakarta.persistence.*;

@Entity
public class CampaignPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    private Long userId;
    private boolean opened;
    private boolean converted;
    private String channel; // EMAIL, SMS, etc.

    public CampaignPerformance() {}

    public CampaignPerformance(Campaign campaign, Long userId, String channel) {
        this.campaign = campaign;
        this.userId = userId;
        this.channel = channel;
        this.opened = false;
        this.converted = false;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public boolean isOpened() { return opened; }
    public void setOpened(boolean opened) { this.opened = opened; }
    public boolean isConverted() { return converted; }
    public void setConverted(boolean converted) { this.converted = converted; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
