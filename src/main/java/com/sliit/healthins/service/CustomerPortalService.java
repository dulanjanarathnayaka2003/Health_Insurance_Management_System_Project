package com.sliit.healthins.service;

import com.sliit.healthins.dto.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.*;
import com.sliit.healthins.model.InquiryStatus;
import com.sliit.healthins.repository.*;
import com.sliit.healthins.util.EmailSenderUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerPortalService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerPortalService.class);
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final PaymentRepository paymentRepository;
    private final InquiryRepository inquiryRepository;
    private final PolicyInfoRepository policyInfoRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;
    private final EmailSenderUtil emailUtil;

    @Autowired
    public CustomerPortalService(PolicyRepository policyRepository, UserRepository userRepository, ClaimRepository claimRepository, PaymentRepository paymentRepository, InquiryRepository inquiryRepository, PolicyInfoRepository policyInfoRepository, BankAccountRepository bankAccountRepository, ModelMapper modelMapper, EmailSenderUtil emailUtil) {
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.claimRepository = claimRepository;
        this.paymentRepository = paymentRepository;
        this.inquiryRepository = inquiryRepository;
        this.policyInfoRepository = policyInfoRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.modelMapper = modelMapper;
        this.emailUtil = emailUtil;
    }
    public List<PaymentDTO> getPayments(Long userId) {
        try {
            logger.info("Getting payments for user ID: {}", userId);
            List<Payment> payments = paymentRepository.findByPolicyCustomerId(userId);
            List<PaymentDTO> paymentDTOs = payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
            logger.info("Successfully retrieved {} payments for user ID: {}", paymentDTOs.size(), userId);
            return paymentDTOs;
        } catch (Exception e) {
            logger.error("Error getting payments for user ID: " + userId, e);
            throw e;
        }
    }

    public List<Payment> getPaymentStatus(Long policyId) {
        return paymentRepository.findByPolicy_Id(policyId);
    }

    public ProfileDTO getProfile(Long userId) {
        try {
            logger.info("Getting profile for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            logger.debug("Found user: {}, role: {}", user.getUsername(), user.getRole());
            
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setName(user.getName());
            profileDTO.setUsername(user.getUsername());
            profileDTO.setEmail(user.getEmail());
            profileDTO.setPhone(user.getPhone());
            profileDTO.setActive(user.isActive());
            profileDTO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
            
            // Add bank account information if available
            if (user.getBankAccount() != null) {
                try {
                    BankAccount bankAccount = user.getBankAccount();
                    profileDTO.setBankName(bankAccount.getBankName());
                    profileDTO.setAccountNumber(bankAccount.getAccountNumber());
                    profileDTO.setAccountHolderName(bankAccount.getAccountHolderName());
                    profileDTO.setBranch(bankAccount.getBranch());
                } catch (Exception e) {
                    logger.warn("Could not load bank account for user ID: {}", userId, e);
                }
            }
            
            logger.debug("Getting policy count for user ID: {}", userId);
            int totalPolicies = (int) policyRepository.countByCustomerIdAndStatus(userId, PolicyStatus.ACTIVE);
            profileDTO.setTotalPolicies(totalPolicies);
            logger.debug("Total active policies: {}", totalPolicies);
            
            logger.debug("Getting claim counts for user ID: {}", userId);
            int totalClaims = (int) claimRepository.countByPolicyCustomerId(userId);
            int pendingClaims = (int) claimRepository.countByPolicyCustomerIdAndStatus(userId, ClaimStatus.PENDING);
            profileDTO.setTotalClaims(totalClaims);
            profileDTO.setPendingClaims(pendingClaims);
            logger.debug("Total claims: {}, Pending claims: {}", totalClaims, pendingClaims);
            
            logger.info("Successfully retrieved profile for user ID: {}", userId);
            return profileDTO;
        } catch (Exception e) {
            logger.error("Error getting profile for user ID: " + userId, e);
            throw e;
        }
    }

    public List<PolicyDetailsDTO> getPolicies(Long userId) {
        List<Policy> policies = policyRepository.findByCustomerId(userId);
        return policies.stream()
            .map(policy -> {
                PolicyDetailsDTO dto = modelMapper.map(policy, PolicyDetailsDTO.class);
                dto.setTotalClaims((int) claimRepository.countByPolicy_Id(policy.getId()));
                dto.setPendingClaims((int) claimRepository.countByPolicy_IdAndStatus(policy.getId(), ClaimStatus.PENDING));
                return dto;
            })
            .collect(Collectors.toList());
    }

    public List<ClaimDTO> getApprovedClaims(Long userId) {
        List<Claim> claims = claimRepository.findByPolicyCustomerIdAndStatus(userId, ClaimStatus.APPROVED);
        return claims.stream()
            .map(claim -> modelMapper.map(claim, ClaimDTO.class))
            .collect(Collectors.toList());
    }

    public PaymentReminderDTO getNextPaymentDue(Long userId) {
        Payment nextPayment = paymentRepository.findFirstByPolicyCustomerIdAndStatusOrderByDueDateAsc(
            userId, PaymentStatus.PENDING)
            .orElse(null);
        
        return nextPayment != null ? modelMapper.map(nextPayment, PaymentReminderDTO.class) : null;
    }

    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO, Long userId) {
        try {
            logger.info("Processing payment for claim ID: {} from user ID: {}", paymentDTO.getClaimId(), userId);
            
            if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0) {
                throw new IllegalArgumentException("Payment amount must be greater than 0");
            }
            
            Claim claim = claimRepository.findById(paymentDTO.getClaimId())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
            
            // Verify claim belongs to user
            if (!claim.getPolicy().getCustomer().getId().equals(userId)) {
                throw new SecurityException("Unauthorized access to claim");
            }
            
            // Create and save payment
            Payment payment = new Payment();
            payment.setPolicy(claim.getPolicy());
            payment.setAmount(BigDecimal.valueOf(paymentDTO.getAmount()));
            payment.setPaymentDate(LocalDate.now());
            payment.setStatus(PaymentStatus.PAID);
            payment.setDueDate(LocalDate.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment saved successfully with ID: {}", savedPayment.getId());
            
            // Update claim status
            claim.setStatus(ClaimStatus.PAID);
            claimRepository.save(claim);
            logger.info("Claim status updated to PAID for claim ID: {}", claim.getId());
            
            PaymentDTO result = modelMapper.map(savedPayment, PaymentDTO.class);
            logger.info("Payment processed successfully");
            return result;
        } catch (Exception e) {
            logger.error("Error processing payment: ", e);
            throw e;
        }
    }

    @Transactional
    public Claim submitClaim(ClaimSubmissionDTO dto, Long userId) {
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("CLAIM SUBMISSION REQUEST RECEIVED");
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("User ID: {}", userId);
        logger.info("Policy ID: {}", dto.getPolicyId());
        logger.info("Claim Amount: {}", dto.getAmount());
        logger.info("Description: {}", dto.getDescription());
        logger.info("Date: {}", dto.getDate());
        
        try {
            // Step 1: Validate User
            logger.info("Step 1: Validating user...");
            User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("❌ User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });
            logger.info("✓ User found: {} ({})", user.getName(), user.getEmail());
            
            // Step 2: Validate Policy
            logger.info("Step 2: Validating policy...");
            Policy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> {
                    logger.error("❌ Policy not found with ID: {}", dto.getPolicyId());
                    return new ResourceNotFoundException("Policy not found");
                });
            logger.info("✓ Policy found: {}", policy.getPolicyNumber());
            logger.info("  - Premium Amount: {}", policy.getPremiumAmount());
            logger.info("  - Status: {}", policy.getStatus());
            logger.info("  - Customer ID: {}", policy.getCustomer().getId());
            
            // Step 3: Validate Policy Ownership
            logger.info("Step 3: Validating policy ownership...");
            if (!policy.getCustomer().getId().equals(userId)) {
                logger.error("❌ Unauthorized access: User {} does not own policy {}", userId, dto.getPolicyId());
                throw new SecurityException("Unauthorized: You do not own this policy");
            }
            logger.info("✓ Policy ownership verified");
            
            // Step 4: Validate Claim Amount
            logger.info("Step 4: Validating claim amount...");
            if (dto.getAmount() == null || dto.getAmount() <= 0) {
                logger.error("❌ Invalid claim amount: {}", dto.getAmount());
                throw new IllegalArgumentException("Claim amount must be greater than zero");
            }
            BigDecimal claimAmount = BigDecimal.valueOf(dto.getAmount());
            if (claimAmount.compareTo(policy.getPremiumAmount()) > 0) {
                logger.error("❌ Claim amount ({}) exceeds policy premium ({})", claimAmount, policy.getPremiumAmount());
                throw new IllegalArgumentException("Claim amount cannot exceed policy premium amount");
            }
            logger.info("✓ Claim amount is valid");
            
            // Step 5: Generate unique claim ID
            logger.info("Step 5: Generating claim ID...");
            String claimId = "CLM-" + System.currentTimeMillis();
            logger.info("✓ Generated Claim ID: {}", claimId);
            
            // Step 6: Create Claim Entity
            logger.info("Step 6: Creating claim entity...");
            Claim claim = new Claim();
            claim.setClaimId(claimId);
            claim.setPolicy(policy);
            claim.setAmount(dto.getAmount().doubleValue());
            claim.setClaimDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
            claim.setNotes(dto.getDescription());
            claim.setStatus(ClaimStatus.PENDING);
            claim.setDocumentPath("N/A"); // No file upload required
            logger.info("✓ Claim entity created");
            
            // Step 7: Save Claim to Database
            logger.info("Step 7: Saving claim to database...");
            Claim savedClaim = claimRepository.save(claim);
            logger.info("✅ Claim saved successfully!");
            logger.info("  - Database ID: {}", savedClaim.getId());
            logger.info("  - Claim ID: {}", savedClaim.getClaimId());
            logger.info("  - Status: {}", savedClaim.getStatus());
            
            // Step 8: Send Email Notification
            logger.info("Step 8: Sending email notification...");
            try {
                emailUtil.sendEmail(user.getEmail(), 
                    "Claim Submitted", 
                    "Your claim has been submitted successfully. Claim ID: " + savedClaim.getClaimId());
                logger.info("✓ Email sent to: {}", user.getEmail());
            } catch (Exception e) {
                logger.warn("⚠ Failed to send email notification for claim: {}", savedClaim.getClaimId(), e);
            }
            
            logger.info("═══════════════════════════════════════════════════════════");
            logger.info("CLAIM SUBMISSION COMPLETED SUCCESSFULLY");
            logger.info("═══════════════════════════════════════════════════════════");
            
            return savedClaim;
        } catch (ResourceNotFoundException e) {
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("CLAIM SUBMISSION FAILED - Resource Not Found");
            logger.error("Error: {}", e.getMessage());
            logger.error("═══════════════════════════════════════════════════════════");
            throw e;
        } catch (SecurityException e) {
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("CLAIM SUBMISSION FAILED - Security Violation");
            logger.error("Error: {}", e.getMessage());
            logger.error("═══════════════════════════════════════════════════════════");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("CLAIM SUBMISSION FAILED - Validation Error");
            logger.error("Error: {}", e.getMessage());
            logger.error("═══════════════════════════════════════════════════════════");
            throw e;
        } catch (Exception e) {
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("CLAIM SUBMISSION FAILED - Unexpected Error");
            logger.error("Error Type: {}", e.getClass().getName());
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Stack Trace:", e);
            logger.error("═══════════════════════════════════════════════════════════");
            throw e;
        }
    }


    public List<Claim> getClaims(Long userId) {
        try {
            logger.info("Getting claims for user ID: {}", userId);
            List<Claim> claims = claimRepository.findByPolicyCustomerId(userId);
            
            // Initialize the policy for each claim to avoid lazy loading issues
            claims.forEach(claim -> {
                logger.debug("Processing claim: ID={}, ClaimID={}, Amount={}, Notes={}, ClaimDate={}", 
                    claim.getId(), claim.getClaimId(), claim.getAmount(), claim.getNotes(), claim.getClaimDate());
                
                Policy policy = claim.getPolicy();
                if (policy != null) {
                    logger.debug("  Policy Number: {}", policy.getPolicyNumber());
                    // Trigger lazy loading by accessing the policy number
                    policy.getPolicyNumber();
                    // Break circular references for JSON serialization
                    policy.setCustomer(null);
                    policy.setClaims(null);
                    policy.setPayments(null);
                } else {
                    logger.warn("  Claim {} has no associated policy!", claim.getId());
                }
            });
            
            logger.info("Successfully retrieved {} claims for user ID: {}", claims.size(), userId);
            return claims;
        } catch (Exception e) {
            logger.error("Error getting claims for user ID: " + userId, e);
            throw e;
        }
    }

    public List<BillingHistoryDTO> getBillingHistory(Long userId) {
        List<Payment> payments = paymentRepository.findByPolicyCustomerId(userId);
        return payments.stream().map(p -> modelMapper.map(p, BillingHistoryDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public void updateContact(Long userId, ContactUpdateDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    @Transactional
    public Claim submitClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public List<Payment> getPaymentStatus(String policyNumber) {
        Policy policy = policyRepository.findByPolicyNumber(policyNumber).orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        return paymentRepository.findByPolicy_Id(policy.getId());
    }

    public List<Inquiry> getInquiryHistory(Long userId) {
        return inquiryRepository.findByCustomerId(userId);
    }

    @Transactional
    public InquiryDTO submitInquiry(InquiryDTO dto, Long userId) {
        try {
            logger.info("Submitting inquiry for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            Inquiry inquiry = new Inquiry();
            inquiry.setCustomer(user);
            inquiry.setType(dto.getType());
            inquiry.setTitle(dto.getTitle());
            inquiry.setDescription(dto.getDescription());
            inquiry.setStatus(InquiryStatus.OPEN);
            
            Inquiry savedInquiry = inquiryRepository.save(inquiry);
            logger.info("Inquiry submitted successfully with ID: {}", savedInquiry.getId());
            
            InquiryDTO result = modelMapper.map(savedInquiry, InquiryDTO.class);
            result.setCustomerId(userId);
            return result;
        } catch (Exception e) {
            logger.error("Error submitting inquiry: ", e);
            throw e;
        }
    }

    public List<InquiryDTO> getInquiries(Long userId) {
        try {
            logger.info("Getting inquiries for user ID: {}", userId);
            List<Inquiry> inquiries = inquiryRepository.findByCustomerId(userId);
            List<InquiryDTO> inquiryDTOs = inquiries.stream()
                .map(inquiry -> {
                    InquiryDTO dto = modelMapper.map(inquiry, InquiryDTO.class);
                    dto.setCustomerId(userId);
                    dto.setSubject(inquiry.getTitle()); // Set subject as alias for title
                    return dto;
                })
                .collect(Collectors.toList());
            logger.info("Successfully retrieved {} inquiries for user ID: {}", inquiryDTOs.size(), userId);
            return inquiryDTOs;
        } catch (Exception e) {
            logger.error("Error getting inquiries for user ID: " + userId, e);
            throw e;
        }
    }

    public List<PolicyInfoDTO> getAvailablePolicies() {
        try {
            logger.info("Getting all available policies");
            List<PolicyInfo> policyInfos = policyInfoRepository.findAll();
            List<PolicyInfoDTO> policyInfoDTOs = policyInfos.stream()
                .map(policyInfo -> {
                    PolicyInfoDTO dto = new PolicyInfoDTO();
                    dto.setId(policyInfo.getId());
                    dto.setCoverageType(policyInfo.getCoverageType());
                    dto.setDescription(policyInfo.getDescription());
                    dto.setBenefits(policyInfo.getBenefits());
                    dto.setCoverageLimit(policyInfo.getCoverageLimit());
                    dto.setPrice(policyInfo.getPrice());
                    return dto;
                })
                .collect(Collectors.toList());
            logger.info("Successfully retrieved {} available policies", policyInfoDTOs.size());
            return policyInfoDTOs;
        } catch (Exception e) {
            logger.error("Error getting available policies", e);
            throw e;
        }
    }

    @Transactional
    public PolicyDetailsDTO purchasePolicy(Long userId, PolicyPurchaseDTO purchaseDTO) {
        try {
            logger.info("Processing policy purchase for user ID: {}", userId);
            
            // Get user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Get policy info
            PolicyInfo policyInfo = policyInfoRepository.findById(purchaseDTO.getPolicyInfoId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy Info not found"));
            
            // Validate payment details (basic validation)
            if (purchaseDTO.getCardNumber() == null || purchaseDTO.getCardNumber().length() < 13) {
                throw new IllegalArgumentException("Invalid card number");
            }
            
            // Create new policy for the customer
            Policy policy = new Policy();
            policy.setPolicyNumber("POL-" + System.currentTimeMillis());
            policy.setCustomer(user);
            policy.setCoverage(policyInfo.getCoverageType());
            policy.setPremiumAmount(policyInfo.getPrice());
            policy.setStatus("ACTIVE");
            policy.setStartDate(LocalDate.now());
            policy.setEndDate(LocalDate.now().plusYears(1)); // 1 year policy
            
            Policy savedPolicy = policyRepository.save(policy);
            logger.info("Policy created with number: {}", savedPolicy.getPolicyNumber());
            
            // Create payment record
            Payment payment = new Payment();
            payment.setPolicy(savedPolicy);
            payment.setAmount(policyInfo.getPrice());
            payment.setDueDate(LocalDate.now());
            payment.setPaymentDate(LocalDate.now());
            payment.setStatus(PaymentStatus.PAID);
            
            paymentRepository.save(payment);
            logger.info("Payment recorded for policy: {}", savedPolicy.getPolicyNumber());
            
            // Send confirmation email
            try {
                String emailBody = "Dear " + user.getName() + ",\n\n" +
                    "Your policy purchase has been confirmed!\n\n" +
                    "Policy Number: " + savedPolicy.getPolicyNumber() + "\n" +
                    "Coverage Type: " + policyInfo.getCoverageType() + "\n" +
                    "Premium Amount: LKR " + policyInfo.getPrice() + "\n" +
                    "Start Date: " + savedPolicy.getStartDate() + "\n" +
                    "End Date: " + savedPolicy.getEndDate() + "\n\n" +
                    "Thank you for choosing HealthInsure!\n\n" +
                    "Best regards,\n" +
                    "HealthInsure Team";
                
                emailUtil.sendEmail(user.getEmail(), "Policy Purchase Confirmation", emailBody);
                logger.info("Confirmation email sent to: {}", user.getEmail());
            } catch (Exception e) {
                logger.warn("Failed to send confirmation email", e);
            }
            
            // Convert to DTO and return
            PolicyDetailsDTO dto = new PolicyDetailsDTO();
            dto.setId(savedPolicy.getId());
            dto.setPolicyNumber(savedPolicy.getPolicyNumber());
            dto.setStatus(savedPolicy.getStatus().name());
            dto.setPremiumAmount(savedPolicy.getPremiumAmount());
            dto.setStartDate(savedPolicy.getStartDate());
            dto.setEndDate(savedPolicy.getEndDate());
            dto.setCoverage(savedPolicy.getCoverage());
            dto.setCoverageType(policyInfo.getCoverageType());
            dto.setDescription(policyInfo.getDescription());
            dto.setBenefits(policyInfo.getBenefits());
            dto.setCoverageLimit(policyInfo.getCoverageLimit());
            
            return dto;
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found during policy purchase: ", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error purchasing policy for user ID: " + userId, e);
            throw new RuntimeException("Failed to process policy purchase: " + e.getMessage());
        }
    }

    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileDTO profileDTO) {
        try {
            logger.info("Updating profile for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Update personal information
            if (profileDTO.getName() != null) {
                user.setName(profileDTO.getName());
            }
            if (profileDTO.getEmail() != null) {
                user.setEmail(profileDTO.getEmail());
            }
            if (profileDTO.getPhone() != null) {
                user.setPhone(profileDTO.getPhone());
            }
            
            userRepository.save(user);
            logger.info("Successfully updated profile for user ID: {}", userId);
            
            // Return updated profile
            return getProfile(userId);
        } catch (Exception e) {
            logger.error("Error updating profile for user ID: " + userId, e);
            throw e;
        }
    }

    @Transactional
    public ProfileDTO updateBankInfo(Long userId, ProfileDTO profileDTO) {
        try {
            logger.info("Updating bank info for user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            BankAccount bankAccount = user.getBankAccount();
            
            if (bankAccount == null) {
                // Create new bank account
                bankAccount = new BankAccount();
                // Update bank account information
                bankAccount.setBankName(profileDTO.getBankName());
                bankAccount.setAccountNumber(profileDTO.getAccountNumber());
                bankAccount.setAccountHolderName(profileDTO.getAccountHolderName());
                bankAccount.setBranch(profileDTO.getBranch());
                
                // Save bank account first
                BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
                
                // Then associate with user
                user.setBankAccount(savedBankAccount);
                userRepository.save(user);
            } else {
                // Update existing bank account information
                if (profileDTO.getBankName() != null) {
                    bankAccount.setBankName(profileDTO.getBankName());
                }
                if (profileDTO.getAccountNumber() != null) {
                    bankAccount.setAccountNumber(profileDTO.getAccountNumber());
                }
                if (profileDTO.getAccountHolderName() != null) {
                    bankAccount.setAccountHolderName(profileDTO.getAccountHolderName());
                }
                if (profileDTO.getBranch() != null) {
                    bankAccount.setBranch(profileDTO.getBranch());
                }
                
                bankAccountRepository.save(bankAccount);
            }
            
            logger.info("Successfully updated bank info for user ID: {}", userId);
            
            // Return updated profile
            return getProfile(userId);
        } catch (Exception e) {
            logger.error("Error updating bank info for user ID: " + userId, e);
            throw e;
        }
    }
}