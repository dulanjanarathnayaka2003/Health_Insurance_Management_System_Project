package com.sliit.healthins.service;

import com.sliit.healthins.dto.CampaignDTO;
import com.sliit.healthins.dto.SegmentDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.Campaign;
import com.sliit.healthins.model.CampaignFeedback;
import com.sliit.healthins.model.CampaignPerformance;
import com.sliit.healthins.model.CampaignStatus;
import com.sliit.healthins.model.CampaignType;
import com.sliit.healthins.model.CustomerSegment;
import com.sliit.healthins.model.Policy;
import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.CampaignFeedbackRepository;
import com.sliit.healthins.repository.CampaignPerformanceRepository;
import com.sliit.healthins.repository.CampaignRepository;
import com.sliit.healthins.repository.CustomerSegmentRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.util.EmailSenderUtil;
import com.sliit.healthins.util.PdfGeneratorUtil;
import com.sliit.healthins.util.SmsSenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MarketingService {

    private final CampaignRepository campaignRepository;
    private final CustomerSegmentRepository customerSegmentRepository;
    private final EmailSenderUtil emailUtil;
    private final SmsSenderUtil smsSenderUtil;
    private final UserRepository userRepository;
    private final PdfGeneratorUtil pdfGeneratorUtil;
    private final CampaignPerformanceRepository campaignPerformanceRepository;
    private final CampaignFeedbackRepository campaignFeedbackRepository;

    @Autowired
    public MarketingService(CampaignRepository campaignRepository, 
                          CustomerSegmentRepository customerSegmentRepository, 
                          EmailSenderUtil emailUtil, 
                          SmsSenderUtil smsSenderUtil, 
                          UserRepository userRepository, 
                          PdfGeneratorUtil pdfGeneratorUtil,
                          CampaignPerformanceRepository campaignPerformanceRepository,
                          CampaignFeedbackRepository campaignFeedbackRepository) {
        this.campaignRepository = campaignRepository;
        this.customerSegmentRepository = customerSegmentRepository;
        this.emailUtil = emailUtil;
        this.smsSenderUtil = smsSenderUtil;
        this.userRepository = userRepository;
        this.pdfGeneratorUtil = pdfGeneratorUtil;
        this.campaignPerformanceRepository = campaignPerformanceRepository;
        this.campaignFeedbackRepository = campaignFeedbackRepository;
    }

    @Transactional
    public Campaign createCampaign(CampaignDTO dto) {
        Campaign campaign = new Campaign();
        campaign.setName(dto.getName());
        campaign.setType(CampaignType.valueOf(dto.getType().toUpperCase()));
        campaign.setStartDate(dto.getStartDate());
        campaign.setEndDate(dto.getEndDate());
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setTargetSegment(dto.getTargetSegment());
        campaign.setDescription(dto.getDescription());
        return campaignRepository.save(campaign);
    }

    public List<CampaignDTO> listCampaigns() {
        return campaignRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new ResourceNotFoundException("Campaign not found with id: " + id);
        }
        campaignRepository.deleteById(id);
    }

    @Transactional
    public CustomerSegment defineSegment(SegmentDTO dto) {
        CustomerSegment segment = new CustomerSegment(dto.getCriteria(), dto.getCustomerIds());
        return customerSegmentRepository.save(segment);
    }

    public Map<String, Integer> getCustomerCount() {
        long customerCount = userRepository.countByRole(Role.CUSTOMER);
        return Map.of("count", (int) customerCount);
    }

    public Map<String, Object> getPolicyTrendsAndDemographics() {
        // Policy trends: most popular plans
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        Map<String, Long> popularPlans = customers.stream()
            .flatMap(user -> user.getPolicies().stream())
            .collect(Collectors.groupingBy(Policy::getCoverage, Collectors.counting()));

        // Demographics: just using contact for now; add fields if gender/region/age exist
        Map<String, Long> byEmailDomain = customers.stream()
            .collect(Collectors.groupingBy(
                user -> {
                    String[] parts = user.getEmail().split("@");
                    return parts.length > 1 ? parts[1] : "unknown";
                }, Collectors.counting()
            ));

        // Add more demographic breakdowns here if fields (age, region, gender) exist in User
        return Map.of(
            "popularPlans", popularPlans, 
            "customerDemographics", byEmailDomain
        );
    }

    @Transactional
    public Map<String, Object> sendSmsToCustomers(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        // Get all customers
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        int sentCount = 0;
        for (User customer : customers) {
            try {
                String message = campaign.getDescription() != null ? campaign.getDescription() : 
                    "Thank you for being our valued customer. We have exciting updates for you!";
                smsSenderUtil.sendSms(customer.getPhone(), message);
                sentCount++;
            } catch (Exception e) {
                System.err.println("Failed to send SMS to " + customer.getPhone() + ": " + e.getMessage());
            }
        }
        return Map.of(
            "sentCount", sentCount,
            "totalCustomers", customers.size(),
            "campaignName", campaign.getName()
        );
    }

    @Transactional
    public Map<String, Object> sendEmailsToCustomers(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        
        // Get all customers
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        int sentCount = 0;
        
        for (User customer : customers) {
            try {
                String subject = "Health Insurance: " + campaign.getName();
                String message = campaign.getDescription() != null ? campaign.getDescription() : 
                               "Thank you for being our valued customer. We have exciting updates for you!";
                
                emailUtil.sendEmail(customer.getEmail(), subject, message);
                sentCount++;
            } catch (Exception e) {
                System.err.println("Failed to send email to " + customer.getEmail() + ": " + e.getMessage());
            }
        }
        
        return Map.of(
            "sentCount", sentCount,
            "totalCustomers", customers.size(),
            "campaignName", campaign.getName()
        );
    }

    @Transactional
    public void sendEmails(Long campaignId, Long segmentId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        CustomerSegment segment = customerSegmentRepository.findById(segmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Segment not found with id: " + segmentId));
        segment.getCustomerIds().forEach(userId -> {
            userRepository.findById(userId).ifPresent(user ->
                    emailUtil.sendEmail(user.getEmail(), campaign.getName(), "Campaign details here")
            );
        });
    }

    @Transactional
    public void trackCampaignOpened(Long campaignId, Long userId, String channel) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        CampaignPerformance perf = new CampaignPerformance(campaign, userId, channel);
        perf.setOpened(true);
        campaignPerformanceRepository.save(perf);
    }

    @Transactional
    public void trackCampaignConverted(Long campaignId, Long userId, String channel) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        CampaignPerformance perf = new CampaignPerformance(campaign, userId, channel);
        perf.setConverted(true);
        campaignPerformanceRepository.save(perf);
    }

    @Transactional
    public void submitCampaignFeedback(Long campaignId, Long userId, String feedbackText, int rating) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        CampaignFeedback feedback = new CampaignFeedback(campaign, userId, feedbackText, rating);
        campaignFeedbackRepository.save(feedback);
    }

    public Map<String, Object> getCampaignFeedback(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + campaignId));
        List<CampaignFeedback> feedbacks = campaignFeedbackRepository.findByCampaign(campaign);
        double avgRating = feedbacks.stream().mapToInt(f -> f.getRating()).average().orElse(0.0);
        List<String> comments = feedbacks.stream().map(f -> f.getFeedbackText()).collect(Collectors.toList());
        return Map.of("averageRating", avgRating, "feedbackCount", feedbacks.size(), "comments", comments);
    }

    public byte[] generateReport(LocalDate start, LocalDate end, String type) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        StringBuilder content = new StringBuilder();
        content.append("Marketing Report\n");
        content.append("Period: ").append(start).append(" to ").append(end).append("\n");
        content.append("Report Type: ").append(type).append("\n\n");

        // Campaigns in this period
        List<Campaign> allCampaigns = campaignRepository.findAll();
        List<Campaign> campaignsInRange = allCampaigns.stream()
                .filter(c -> {
                    LocalDate cStart = c.getStartDate();
                    LocalDate cEnd = c.getEndDate();
                    return (cStart != null && cEnd != null) &&
                           ((cStart.isAfter(start) || cStart.isEqual(start)) && (cStart.isBefore(end) || cStart.isEqual(end)) ||
                            (cEnd.isAfter(start) || cEnd.isEqual(start)) && (cEnd.isBefore(end) || cEnd.isEqual(end)));
                })
                .collect(Collectors.toList());

        content.append("CAMPAIGN STATISTICS\n==================\n");
        content.append("Total Campaigns in Period: ").append(campaignsInRange.size()).append("\n");
        long activeCampaigns = campaignsInRange.stream().filter(c -> c.getStatus() == CampaignStatus.ACTIVE).count();
        long completedCampaigns = campaignsInRange.stream().filter(c -> c.getStatus() == CampaignStatus.COMPLETED).count();
        long draftCampaigns = campaignsInRange.stream().filter(c -> c.getStatus() == CampaignStatus.DRAFT).count();
        content.append("Active: ").append(activeCampaigns).append("\n");
        content.append("Completed: ").append(completedCampaigns).append("\n");
        content.append("Draft: ").append(draftCampaigns).append("\n\n");
        long emailCampaigns = campaignsInRange.stream().filter(c -> c.getType() == CampaignType.EMAIL).count();
        long smsCampaigns = campaignsInRange.stream().filter(c -> c.getType() == CampaignType.SMS).count();
        long socialMediaCampaigns = campaignsInRange.stream().filter(c -> c.getType() == CampaignType.SOCIAL_MEDIA).count();
        content.append("CAMPAIGN TYPES\n==============\n");
        content.append("Email: ").append(emailCampaigns).append("\n");
        content.append("SMS: ").append(smsCampaigns).append("\n");
        content.append("Social Media: ").append(socialMediaCampaigns).append("\n\n");
        // Engagement and Retention Analytics
        content.append("ENGAGEMENT & RETENTION\n======================\n");
        // Use CampaignPerformanceRepository for real stats
        int totalEmailsSent = 0, totalEmailsOpened = 0, totalEmailFailures = 0, totalCampaignConversions = 0;
        for (Campaign c : campaignsInRange) {
            totalEmailsOpened += (int) campaignPerformanceRepository.countByCampaignAndOpenedTrue(c);
            totalCampaignConversions += (int) campaignPerformanceRepository.countByCampaignAndConvertedTrue(c);
            // (Extend to include failures and SMS as needed)
        }
        content.append("Emails Sent: ").append(totalEmailsSent).append("\n");
        content.append("Emails Opened: ").append(totalEmailsOpened).append("\n");
        content.append("Email Failures: ").append(totalEmailFailures).append("\n");
        content.append("Campaign Conversions: ").append(totalCampaignConversions).append("\n\n");
        // Add detailed campaign list if requested
        if ("DETAILED".equals(type) || "CAMPAIGNS".equals(type)) {
            content.append("CAMPAIGN DETAILS\n================\n");
            campaignsInRange.forEach(c -> {
                content.append("  - ID: ").append(c.getId())
                       .append(", Name: ").append(c.getName())
                       .append(", Type: ").append(c.getType())
                       .append(", Status: ").append(c.getStatus())
                       .append(", Start: ").append(c.getStartDate())
                       .append(", End: ").append(c.getEndDate())
                       .append("\n");
                if (c.getDescription() != null && !c.getDescription().isEmpty()) {
                    content.append("    Description: ").append(c.getDescription()).append("\n");
                }
            });
            content.append("\n");
        }
        content.append("\nReport Generated: ").append(LocalDate.now()).append("\n");

        try {
            return pdfGeneratorUtil.generatePdf("Marketing Report - " + type, content.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    private CampaignDTO convertToDTO(Campaign campaign) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        dto.setName(campaign.getName());
        dto.setType(campaign.getType().name());
        dto.setStartDate(campaign.getStartDate());
        dto.setEndDate(campaign.getEndDate());
        dto.setStatus(campaign.getStatus().name());
        dto.setTargetSegment(campaign.getTargetSegment());
        dto.setDescription(campaign.getDescription());
        return dto;
    }

    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    @Transactional
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }
}
