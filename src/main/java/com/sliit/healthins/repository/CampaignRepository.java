package com.sliit.healthins.repository;

import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStartDateAfterAndEndDateBefore(LocalDate start, LocalDate end);
    List<Campaign> findByStatus(CampaignStatus status);
}