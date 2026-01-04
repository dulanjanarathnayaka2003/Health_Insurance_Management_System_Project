package com.sliit.healthins.repository;

import com.sliit.healthins.model.CampaignPerformance;
import com.sliit.healthins.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignPerformanceRepository extends JpaRepository<CampaignPerformance, Long> {
    List<CampaignPerformance> findByCampaign(Campaign campaign);
    long countByCampaignAndOpenedTrue(Campaign campaign);
    long countByCampaignAndConvertedTrue(Campaign campaign);
}
