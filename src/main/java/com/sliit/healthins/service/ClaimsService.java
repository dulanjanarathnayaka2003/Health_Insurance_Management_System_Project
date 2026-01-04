package com.sliit.healthins.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sliit.healthins.dto.ClaimDTO;
import com.sliit.healthins.dto.ClaimSubmissionDTO;
import com.sliit.healthins.dto.ClaimUpdateDTO;
import com.sliit.healthins.dto.UserDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.model.ClaimStatus;
import com.sliit.healthins.repository.ClaimRepository;
import com.sliit.healthins.repository.PolicyRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.util.EmailSenderUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClaimsService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimsService.class);
    
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final EmailSenderUtil emailUtil;
    private final ModelMapper modelMapper;

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    public ClaimsService(ClaimRepository claimRepository, PolicyRepository policyRepository, UserRepository userRepository, EmailSenderUtil emailUtil, ModelMapper modelMapper) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.emailUtil = emailUtil;
        this.modelMapper = modelMapper;
    }

    private String saveFile(MultipartFile file) {
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Transactional
    public Claim submitClaim(ClaimSubmissionDTO dto, MultipartFile file) {
        policyRepository.findById(dto.getPolicyId())
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + dto.getPolicyId()));
        String filePath = saveFile(file);
        Claim claim = modelMapper.map(dto, Claim.class);
        claim.setDocumentPath(filePath);
        claim.setStatus(ClaimStatus.PENDING);
        claim.setClaimDate(LocalDate.now());
        Claim savedClaim = claimRepository.save(claim);
        emailUtil.sendEmail("claim@healthins.com", "Claim Submitted", "Your claim ID: " + savedClaim.getId());
        return savedClaim;
    }

    @Transactional
    public Claim updateClaimStatus(Long id, ClaimUpdateDTO dto) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        ClaimStatus newStatus = ClaimStatus.valueOf(dto.getStatus().toUpperCase());
        if (claim.getStatus() == ClaimStatus.APPROVED && newStatus == ClaimStatus.REJECTED) {
            throw new IllegalStateException("Cannot transition from APPROVED to REJECTED");
        }
        claim.setStatus(newStatus);
        return claimRepository.save(claim);
    }

    public List<ClaimDTO> reviewClaims(String query) {
        ClaimStatus status;
        try {
            status = ClaimStatus.valueOf(query.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid claim status: " + query);
        }
        List<Claim> claims = claimRepository.findByStatus(status);
        return claims.stream().map(claim -> modelMapper.map(claim, ClaimDTO.class)).collect(Collectors.toList());
    }

    public byte[] generateReport(LocalDate start, LocalDate end) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph("Claims Report from " + start + " to " + end));
            PdfPTable table = new PdfPTable(5);
            addTableHeader(table, "ID", "Policy Number", "Status", "Claim Date", "Notes");
            claimRepository.findByClaimDateBetween(start, end).forEach(claim -> {
                table.addCell(String.valueOf(claim.getId()));
                table.addCell(claim.getPolicy().getPolicyNumber());
                table.addCell(claim.getStatus().name());
                table.addCell(claim.getClaimDate().toString());
                table.addCell(claim.getNotes() != null ? claim.getNotes() : "N/A");
            });
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Report generation failed: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header));
            table.addCell(cell);
        }
    }

    public Claim submitClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    @Transactional
    public Claim submitClaimByStaff(ClaimSubmissionDTO dto) {
        // Find policy by policy number
        var policy = policyRepository.findByPolicyNumber(dto.getPolicyNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with number: " + dto.getPolicyNumber()));
        
        // Verify the policy belongs to the specified user
        if (!policy.getCustomer().getId().equals(dto.getUserId())) {
            throw new IllegalArgumentException("Policy does not belong to the specified user");
        }
        
        Claim claim = new Claim();
        claim.setClaimId("CLM-" + System.currentTimeMillis());
        claim.setPolicy(policy);
        claim.setAmount(dto.getAmount());
        claim.setClaimDate(dto.getClaimDate() != null ? dto.getClaimDate() : LocalDate.now());
        claim.setDescription(dto.getDescription());
        claim.setNotes(dto.getNotes());
        claim.setStatus(ClaimStatus.PENDING);
        claim.setDocumentPath("N/A");
        
        return claimRepository.save(claim);
    }

    public List<ClaimDTO> getPendingClaims() {
        List<Claim> claims = claimRepository.findByStatus(ClaimStatus.PENDING);
        return claims.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ClaimDTO> getAllClaims() {
        List<Claim> claims = claimRepository.findAll();
        return claims.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ClaimDTO> searchClaims(String query) {
        List<Claim> claims;
        
        // Try to search by claim ID
        if (query.startsWith("CLM-")) {
            var claim = claimRepository.findByClaimId(query);
            claims = claim.map(List::of).orElse(List.of());
        } else {
            // Try to parse as user ID
            try {
                Long userId = Long.parseLong(query);
                claims = claimRepository.findByPolicyCustomerId(userId);
            } catch (NumberFormatException e) {
                // Search by policy number
                var policy = policyRepository.findByPolicyNumber(query);
                claims = policy.map(p -> claimRepository.findByPolicy(p)).orElse(List.of());
            }
        }
        
        return claims.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ClaimDTO getClaimById(String claimId) {
        Claim claim = claimRepository.findByClaimId(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));
        return mapToDTO(claim);
    }

    public byte[] generateClaimsReport(LocalDate start, LocalDate end, String type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            document.add(new Paragraph("Claims Report"));
            document.add(new Paragraph("Period: " + start + " to " + end));
            document.add(new Paragraph("Type: " + type));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(6);
            addTableHeader(table, "Claim ID", "User", "Policy", "Amount", "Date", "Status");
            
            List<Claim> claims;
            if (type.equals("ALL")) {
                claims = claimRepository.findByClaimDateBetween(start, end);
            } else {
                ClaimStatus status = ClaimStatus.valueOf(type);
                claims = claimRepository.findByClaimDateBetween(start, end).stream()
                        .filter(c -> c.getStatus() == status)
                        .collect(Collectors.toList());
            }
            
            claims.forEach(claim -> {
                table.addCell(claim.getClaimId());
                table.addCell(claim.getPolicy().getCustomer().getName());
                table.addCell(claim.getPolicy().getPolicyNumber());
                table.addCell(String.format("$%.2f", claim.getAmount()));
                table.addCell(claim.getClaimDate().toString());
                table.addCell(claim.getStatus().name());
            });
            
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Report generation failed: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    private ClaimDTO mapToDTO(Claim claim) {
        ClaimDTO dto = modelMapper.map(claim, ClaimDTO.class);
        if (claim.getPolicy() != null) {
            dto.setPolicyNumber(claim.getPolicy().getPolicyNumber());
            dto.setPolicyType(claim.getPolicy().getCoverage());
            if (claim.getPolicy().getCustomer() != null) {
                dto.setUserId(claim.getPolicy().getCustomer().getId());
                dto.setUserName(claim.getPolicy().getCustomer().getName());
            }
        }
        dto.setDescription(claim.getNotes()); // Use notes as description if not set
        return dto;
    }

    @Transactional
    public ClaimDTO updateClaimStatusByClaimId(String claimId, ClaimUpdateDTO dto) {
        logger.info("=== SERVICE: updateClaimStatusByClaimId ===");
        logger.info("Searching for claim with ID: {}", claimId);
        
        Claim claim = claimRepository.findByClaimId(claimId)
                .orElseThrow(() -> {
                    logger.error("Claim not found with ID: {}", claimId);
                    return new ResourceNotFoundException("Claim not found with ID: " + claimId);
                });
        
        logger.info("Found claim: ID={}, Current Status={}", claim.getClaimId(), claim.getStatus());
        
        try {
            ClaimStatus newStatus = ClaimStatus.valueOf(dto.getStatus());
            logger.info("Updating status from {} to {}", claim.getStatus(), newStatus);
            
            claim.setStatus(newStatus);
            
            // Append new notes to existing notes
            if (dto.getNotes() != null && !dto.getNotes().isEmpty()) {
                String existingNotes = claim.getNotes() != null ? claim.getNotes() : "";
                String updatedNotes = existingNotes + (existingNotes.isEmpty() ? "" : "\n") + dto.getNotes();
                claim.setNotes(updatedNotes);
                logger.info("Updated notes: {}", updatedNotes);
            }
            
            Claim saved = claimRepository.save(claim);
            logger.info("Claim saved successfully. New status: {}", saved.getStatus());
            
            ClaimDTO result = mapToDTO(saved);
            logger.info("Returning DTO with status: {}", result.getStatus());
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid claim status: {}", dto.getStatus(), e);
            throw new IllegalArgumentException("Invalid claim status: " + dto.getStatus());
        }
    }
    
    /**
     * Get customer bank details for payment processing
     */
    public UserDTO getCustomerBankDetails(Long userId) {
        logger.info("Fetching bank details for user ID: {}", userId);
        
        com.sliit.healthins.model.User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Convert to DTO
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setUsername(user.getUsername());
        // Note: UserDTO role field might be String or need conversion
        // dto.setRole(user.getRole());
        
        // Add bank account details if available
        if (user.getBankAccount() != null) {
            try {
                // Force initialization of lazy-loaded bank account
                user.getBankAccount().getBankName();
                
                dto.setBankName(user.getBankAccount().getBankName());
                dto.setAccountNumber(user.getBankAccount().getAccountNumber());
                dto.setAccountHolderName(user.getBankAccount().getAccountHolderName());
                dto.setBranch(user.getBankAccount().getBranch());
                
                logger.info("Bank details found for user {}: Bank={}, Account=***{}",
                        userId, user.getBankAccount().getBankName(),
                        user.getBankAccount().getAccountNumber().substring(Math.max(0, user.getBankAccount().getAccountNumber().length() - 4)));
            } catch (Exception e) {
                logger.warn("Could not load bank account details for user {}: {}", userId, e.getMessage());
            }
        } else {
            logger.warn("No bank account found for user {}", userId);
        }
        
        return dto;
    }
}