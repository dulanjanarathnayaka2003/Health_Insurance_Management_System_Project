package com.sliit.healthins;

import com.sliit.healthins.dto.SegmentDTO;
import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CampaignType;
import com.sliit.healthins.model.CustomerSegment;
import com.sliit.healthins.repository.CampaignRepository;
import com.sliit.healthins.service.MarketingService;
import com.sliit.healthins.util.EmailSenderUtil;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MarketingServiceTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Getter
    @Setter
    @Mock
    private EmailSenderUtil emailUtil;

    @InjectMocks
    private MarketingService marketingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCampaign_Success() {
        Campaign campaign = new Campaign();
        campaign.setName("New Campaign");
        campaign.setType(CampaignType.EMAIL_BLAST);

        when(campaignRepository.save(campaign)).thenReturn(campaign);

        Campaign createdCampaign = marketingService.createCampaign(campaign);

        assertNotNull(createdCampaign);
        assertEquals("New Campaign", createdCampaign.getName());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testGetCampaignById_Success() {
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("New Campaign");

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        Optional<Campaign> foundCampaign = marketingService.getCampaignById(1L);

        assertTrue(foundCampaign.isPresent());
        assertEquals("New Campaign", foundCampaign.get().getName());
        verify(campaignRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateCampaign_InvalidDates() {
        Campaign campaign = new Campaign();
        campaign.setStartDate(java.time.LocalDate.now().plusDays(1));
        campaign.setEndDate(java.time.LocalDate.now());
        // Add date validation if required in MarketingService
        assertDoesNotThrow(() -> marketingService.createCampaign(campaign));
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testDefineSegment_Success() {
        CustomerSegment segment = new CustomerSegment("age > 30", List.of(1L));
        when(campaignRepository.save(any())).thenReturn(segment);

        CustomerSegment createdSegment = marketingService.defineSegment(new SegmentDTO("age > 30", List.of(1L)));

        assertNotNull(createdSegment);
        verify(campaignRepository, times(1)).save(any());
    }

}