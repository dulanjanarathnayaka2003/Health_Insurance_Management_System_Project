package com.sliit.healthins.repository;

import com.sliit.healthins.model.CampaignFeedback;
import com.sliit.healthins.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignFeedbackRepository extends JpaRepository<CampaignFeedback, Long> {
    List<CampaignFeedback> findByCampaign(Campaign campaign);
    long countByCampaign(Campaign campaign);
    double countByCampaignAndRatingGreaterThanEqual(Campaign campaign, int minRating);
}
