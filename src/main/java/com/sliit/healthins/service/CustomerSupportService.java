package com.sliit.healthins.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sliit.healthins.dto.*;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.*;
import com.sliit.healthins.repository.*;
import com.sliit.healthins.util.EmailSenderUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerSupportService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSupportService.class);

    private final EmailSenderUtil emailSenderUtil;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PolicyInfoRepository policyInfoRepository;
    private final PaymentRepository paymentRepository;
    private final ClaimRepository claimRepository;
    private final InquiryRepository inquiryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomerSupportService(EmailSenderUtil emailSenderUtil, UserRepository userRepository,
                                  PolicyRepository policyRepository, PolicyInfoRepository policyInfoRepository,
                                  PaymentRepository paymentRepository, ClaimRepository claimRepository,
                                  InquiryRepository inquiryRepository, ModelMapper modelMapper) {
        this.emailSenderUtil = emailSenderUtil;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.policyInfoRepository = policyInfoRepository;
        this.paymentRepository = paymentRepository;
        this.claimRepository = claimRepository;
        this.inquiryRepository = inquiryRepository;
        this.modelMapper = modelMapper;
    }

    public DashboardStatsDTO getDashboardStats() {
        logger.info("Getting dashboard stats");
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalCustomers(userRepository.countByRole(Role.POLICYHOLDER));
        stats.setPendingClaims(claimRepository.countByStatus(ClaimStatus.PENDING));
        stats.setResolvedInquiries(inquiryRepository.countByStatus(InquiryStatus.RESOLVED));
        stats.setOverduePayments(paymentRepository.countByStatus(PaymentStatus.OVERDUE));
        logger.info("Dashboard stats retrieved successfully");
        return stats;
    }

    public List<CustomerDTO> getAllCustomers() {
        logger.info("Getting all customers from database");
        List<User> users = userRepository.findAll();
        logger.info("Found {} total users in database", users.size());
        // Filter to only include CUSTOMER and POLICYHOLDER roles
        List<CustomerDTO> customers = users.stream()
                .filter(user -> user.getRole() == Role.CUSTOMER || user.getRole() == Role.POLICYHOLDER)
                .map(this::mapUserToCustomerDTO)
                .collect(Collectors.toList());
        logger.info("Filtered to {} customers from {} total users", customers.size(), users.size());
        return customers;
    }

    public CustomerDTO getCustomer(Long id) {
        logger.info("Getting customer with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return mapUserToCustomerDTO(user);
    }

    public List<CustomerDTO> searchCustomers(String query) {
        logger.info("Searching customers with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        List<User> users = userRepository.findByNameContainingIgnoreCaseOrContactContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        logger.info("Search found {} total users", users.size());
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        // Filter to only include CUSTOMER and POLICYHOLDER roles
        List<CustomerDTO> customers = users.stream()
                .filter(user -> user.getRole() == Role.CUSTOMER || user.getRole() == Role.POLICYHOLDER)
                .map(this::mapUserToCustomerDTO)
                .collect(Collectors.toList());
        logger.info("Filtered to {} customers", customers.size());
        return customers;
    }

    private CustomerDTO mapUserToCustomerDTO(User user) {
        CustomerDTO dto = modelMapper.map(user, CustomerDTO.class);
        dto.setStatus(user.getPolicies() != null && !user.getPolicies().isEmpty() ? user.getPolicies().getFirst().getStatus().name() : "N/A");
        dto.setPolicyNumber(user.getPolicies() != null && !user.getPolicies().isEmpty() ? user.getPolicies().getFirst().getPolicyNumber() : "N/A");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsActive(user.isActive());
        return dto;
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        modelMapper.map(customerDTO, user);
        user = userRepository.save(user);
        return modelMapper.map(user, CustomerDTO.class);
    }

    @Transactional
    public PolicyDetailsDTO updatePolicy(String policyNumber, PolicyDetailsDTO policyDTO) {
        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with number: " + policyNumber));
        modelMapper.map(policyDTO, policy);
        policy = policyRepository.save(policy);
        return modelMapper.map(policy, PolicyDetailsDTO.class);
    }

    @Transactional
    public ClaimUpdateDTO updateClaimStatus(Long id, ClaimUpdateDTO claimDTO) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + id));
        if (claimDTO.getStatus() != null) {
            claim.setStatus(ClaimStatus.valueOf(claimDTO.getStatus().toUpperCase()));
        }
        claim.setNotes(claimDTO.getNotes());
        claim = claimRepository.save(claim);
        return modelMapper.map(claim, ClaimUpdateDTO.class);
    }

    public List<ClaimUpdateDTO> getClaims() {
        return claimRepository.findAll().stream()
                .map(claim -> modelMapper.map(claim, ClaimUpdateDTO.class))
                .collect(Collectors.toList());
    }

    public byte[] generateReport(String reportType, String fromDate, String toDate) {
        logger.info("Starting report generation for type: {}, from: {}, to: {}", reportType, fromDate, toDate);
        
        // Validate inputs
        if (reportType == null || reportType.trim().isEmpty()) {
            throw new IllegalArgumentException("Report type cannot be empty");
        }
        if (fromDate == null || fromDate.trim().isEmpty()) {
            throw new IllegalArgumentException("From date cannot be empty");
        }
        if (toDate == null || toDate.trim().isEmpty()) {
            throw new IllegalArgumentException("To date cannot be empty");
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = null;
        
        try {
            // Parse dates with proper validation
            LocalDate from = LocalDate.parse(fromDate, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate to = LocalDate.parse(toDate, DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Validate date range
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("From date cannot be after to date");
            }
            
            logger.info("Parsed dates successfully: from={}, to={}", from, to);
            
            // Create PDF document
            document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add title and header
            document.add(new Paragraph(reportType.toUpperCase() + " REPORT"));
            document.add(new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            document.add(new Paragraph("Date Range: " + fromDate + " to " + toDate));
            document.add(new Paragraph(" ")); // Empty line
            
            // Get data based on report type
            List<?> data = getReportData(reportType.toLowerCase(), from, to);
            logger.info("Retrieved {} records for {} report", data.size(), reportType);
            
            if (data.isEmpty()) {
                document.add(new Paragraph("No data available for the selected date range and report type."));
            } else {
                // Create table with appropriate columns
                PdfPTable table = createReportTable(reportType.toLowerCase());
                addTableData(table, data, reportType.toLowerCase());
                document.add(table);
            }
            
            document.close();
            logger.info("PDF document generated successfully, size: {} bytes", baos.size());
            
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
        } catch (DocumentException e) {
            logger.error("PDF generation error: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF document: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during report generation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
        
        byte[] result = baos.toByteArray();
        if (result.length == 0) {
            throw new RuntimeException("Generated PDF is empty");
        }
        
        return result;
    }
    
    private List<?> getReportData(String reportType, LocalDate from, LocalDate to) {
        return switch (reportType) {
            case "claims" -> {
                logger.info("Fetching claims data from {} to {}", from, to);
                yield claimRepository.findByClaimDateBetween(from, to);
            }
            case "inquiries" -> {
                logger.info("Fetching inquiries data from {} to {}", from, to);
                yield inquiryRepository.findByResolutionDateBetween(from, to);
            }
            case "payments" -> {
                logger.info("Fetching payments data from {} to {}", from, to);
                yield paymentRepository.findByPaymentDateBetween(from.atStartOfDay(), to.atTime(23, 59, 59));
            }
            default -> {
                logger.warn("Unknown report type: {}", reportType);
                yield Collections.emptyList();
            }
        };
    }
    
    private PdfPTable createReportTable(String reportType) {
        PdfPTable table = switch (reportType) {
            case "claims" -> new PdfPTable(4);
            case "inquiries" -> new PdfPTable(4);
            case "payments" -> new PdfPTable(4);
            default -> new PdfPTable(4);
        };
        table.setWidthPercentage(100);
        return table;
    }
    
    private void addTableData(PdfPTable table, List<?> data, String reportType) {
        // Add headers
        addTableHeader(table, reportType);
        
        // Add data rows
        for (Object item : data) {
            if (item instanceof Claim claim) {
                table.addCell(claim.getId() != null ? String.valueOf(claim.getId()) : "N/A");
                table.addCell(claim.getPolicy() != null ? claim.getPolicy().getPolicyNumber() : "N/A");
                table.addCell(claim.getStatus() != null ? claim.getStatus().name() : "N/A");
                table.addCell(claim.getNotes() != null ? claim.getNotes() : "N/A");
            } else if (item instanceof Inquiry inquiry) {
                table.addCell(inquiry.getId() != null ? String.valueOf(inquiry.getId()) : "N/A");
                table.addCell(inquiry.getType() != null ? inquiry.getType() : "N/A");
                table.addCell(inquiry.getStatus() != null ? inquiry.getStatus().name() : "N/A");
                table.addCell(inquiry.getResolutionDate() != null ? inquiry.getResolutionDate().toString() : "N/A");
            } else if (item instanceof Payment payment) {
                String idStr = Optional.of(payment)
                        .map(Payment::getId)
                        .map(String::valueOf)
                        .orElse("N/A");
                table.addCell(idStr);
                table.addCell(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : "N/A");
                table.addCell(payment.getStatus() != null ? payment.getStatus().name() : "N/A");
                table.addCell(payment.getAmount() != null ? payment.getAmount().toString() : "N/A");
            }
        }
    }
    
    private void addTableHeader(PdfPTable table, String reportType) {
        String[] headers = switch (reportType) {
            case "claims" -> new String[]{"ID", "Policy Number", "Status", "Notes"};
            case "inquiries" -> new String[]{"ID", "Type", "Status", "Resolution Date"};
            case "payments" -> new String[]{"ID", "Policy Number", "Status", "Amount"};
            default -> new String[]{"ID", "Details", "Status", "Notes"};
        };
        
        for (String header : headers) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Paragraph(header));
            cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    public List<InquiryDTO> getPendingInquiries() {
        List<Inquiry> inquiries = inquiryRepository.findByStatus(InquiryStatus.OPEN);
        if (inquiries.isEmpty()) {
            return Collections.emptyList();
        }
        return inquiries.stream()
                .map(inquiry -> new InquiryDTO(
                        inquiry.getId(),
                        inquiry.getCustomer() != null ? inquiry.getCustomer().getId() : null,
                        inquiry.getType(),
                        inquiry.getDescription(),
                        inquiry.getStatus().name(),
                        inquiry.getResolutionDate()
                ))
                .collect(Collectors.toList());
    }

    public List<InquiryDTO> getAllInquiries() {
        logger.info("Getting all inquiries");
        List<Inquiry> inquiries = inquiryRepository.findAll();
        return inquiries.stream()
                .map(inquiry -> {
                    InquiryDTO dto = new InquiryDTO(
                            inquiry.getId(),
                            inquiry.getCustomer() != null ? inquiry.getCustomer().getId() : null,
                            inquiry.getType(),
                            inquiry.getDescription(),
                            inquiry.getStatus().name(),
                            inquiry.getResolutionDate()
                    );
                    dto.setTitle(inquiry.getTitle());
                    dto.setCreatedAt(inquiry.getCreatedAt());
                    dto.setCustomerName(inquiry.getCustomer() != null ? inquiry.getCustomer().getName() : "Unknown");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<InquiryDTO> searchInquiries(String query) {
        logger.info("Searching inquiries with query: {}", query);
        List<Inquiry> inquiries;
        
        // Try to parse as customer ID
        try {
            Long customerId = Long.parseLong(query);
            inquiries = inquiryRepository.findByCustomerId(customerId);
        } catch (NumberFormatException e) {
            // Search by customer name
            List<User> users = userRepository.findByNameContainingIgnoreCase(query);
            inquiries = new java.util.ArrayList<>();
            for (User user : users) {
                inquiries.addAll(inquiryRepository.findByCustomerId(user.getId()));
            }
        }
        
        return inquiries.stream()
                .map(inquiry -> {
                    InquiryDTO dto = new InquiryDTO(
                            inquiry.getId(),
                            inquiry.getCustomer() != null ? inquiry.getCustomer().getId() : null,
                            inquiry.getType(),
                            inquiry.getDescription(),
                            inquiry.getStatus().name(),
                            inquiry.getResolutionDate()
                    );
                    dto.setTitle(inquiry.getTitle());
                    dto.setCreatedAt(inquiry.getCreatedAt());
                    dto.setCustomerName(inquiry.getCustomer() != null ? inquiry.getCustomer().getName() : "Unknown");
                    dto.setResponse(inquiry.getTitle()); // Using title as a response for now
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public InquiryDTO updateInquiry(Long id, InquiryDTO inquiryDTO) {
        logger.info("Updating inquiry ID: {} with status: {}", id, inquiryDTO.getStatus());
        if (id == null || inquiryDTO == null) {
            throw new IllegalArgumentException("Inquiry ID and data are required");
        }
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with ID: " + id));
        
        if (inquiryDTO.getStatus() != null && !inquiryDTO.getStatus().isEmpty()) {
            inquiry.setStatus(InquiryStatus.valueOf(inquiryDTO.getStatus().toUpperCase()));
            if (inquiryDTO.getStatus().equalsIgnoreCase("RESOLVED")) {
                inquiry.setResolutionDate(LocalDate.now());
            }
        }
        
        inquiry = inquiryRepository.save(inquiry);
        logger.info("Successfully updated inquiry ID: {}", id);
        
        InquiryDTO result = modelMapper.map(inquiry, InquiryDTO.class);
        result.setCustomerId(inquiry.getCustomer() != null ? inquiry.getCustomer().getId() : null);
        result.setCustomerName(inquiry.getCustomer() != null ? inquiry.getCustomer().getName() : "Unknown");
        return result;
    }

    @Transactional
    public void sendPaymentReminder(PaymentReminderDTO reminderDTO) {
        Payment payment = paymentRepository.findById(reminderDTO.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + reminderDTO.getPaymentId()));
        String subject = "Payment Reminder - " + reminderDTO.getReminderType();
        String message = "Dear " + payment.getCustomer().getName() + ",\n\n" +
                "This is a " + reminderDTO.getReminderType().toLowerCase() + " reminder for your payment due. " +
                "Amount: " + payment.getAmount() + ", Due Date: " + payment.getDueDate() + ".\n\n" +
                "Please settle at your earliest convenience.\n\nRegards,\nHealthInsure Team";
        emailSenderUtil.sendEmail(payment.getCustomer().getEmail(), subject, message);
    }


    public List<PolicyDetailsDTO> getAllPolicyInfo() {
        logger.info("Getting all policy info from policy_info table");
        List<PolicyInfo> policyInfos = policyInfoRepository.findAll();
        return policyInfos.stream()
                .map(info -> {
                    PolicyDetailsDTO dto = new PolicyDetailsDTO();
                    dto.setCoverageType(info.getCoverageType());
                    dto.setDescription(info.getDescription());
                    dto.setBenefits(info.getBenefits());
                    dto.setCoverageLimit(info.getCoverageLimit());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PolicyDetailsDTO> getCustomerPolicies(Long customerId) {
        logger.info("Getting policies for customer ID: {}", customerId);
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        return user.getPolicies().stream()
                .map(policy -> {
                    PolicyDetailsDTO dto = modelMapper.map(policy, PolicyDetailsDTO.class);
                    dto.setCustomerId(customerId);
                    dto.setTotalClaims((int) claimRepository.countByPolicy_Id(policy.getId()));
                    dto.setPendingClaims((int) claimRepository.countByPolicy_IdAndStatus(policy.getId(), ClaimStatus.PENDING));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PolicyDetailsDTO addPolicy(PolicyDetailsDTO policyDTO) {
        logger.info("Adding new policy for customer ID: {}", policyDTO.getCustomerId());
        User customer = userRepository.findById(policyDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + policyDTO.getCustomerId()));
        
        // Generate policy number
        String policyNumber = "POL-" + System.currentTimeMillis();
        
        Policy policy = new Policy(
                policyNumber,
                PolicyStatus.valueOf(policyDTO.getStatus()),
                policyDTO.getPremiumAmount(),
                policyDTO.getStartDate(),
                policyDTO.getEndDate(),
                customer,
                policyDTO.getCoverage()
        );
        
        policy = policyRepository.save(policy);
        logger.info("Policy created with number: {}", policyNumber);
        
        PolicyDetailsDTO result = modelMapper.map(policy, PolicyDetailsDTO.class);
        result.setCustomerId(policyDTO.getCustomerId());
        return result;
    }

    @Transactional
    public void deletePolicy(String policyNumber) {
        logger.info("Deleting policy: {}", policyNumber);
        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with number: " + policyNumber));
        policyRepository.delete(policy);
        logger.info("Policy deleted successfully: {}", policyNumber);
    }

    public List<PaymentReminderDTO> getAllPayments() {
        logger.info("Getting all payments for reminder management");
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(payment -> {
                    PaymentReminderDTO dto = new PaymentReminderDTO();
                    dto.setPaymentId(payment.getId());
                    dto.setAmount(payment.getAmount());
                    dto.setDueDate(payment.getDueDate());
                    dto.setPolicyNumber(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : "N/A");
                    dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "N/A");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PaymentReminderDTO> searchPayments(String query) {
        logger.info("Searching payments with query: {}", query);
        List<Payment> payments = new java.util.ArrayList<>();
        
        // Try to parse as payment ID
        try {
            Long paymentId = Long.parseLong(query);
            Optional<Payment> payment = paymentRepository.findById(paymentId);
            if (payment.isPresent()) {
                payments.add(payment.get());
            }
        } catch (NumberFormatException e) {
            // Search by policy number - find exact match first
            Optional<Policy> policy = policyRepository.findByPolicyNumber(query);
            if (policy.isPresent()) {
                payments.addAll(paymentRepository.findByPolicy_Id(policy.get().getId()));
            }
        }
        
        return payments.stream()
                .map(payment -> {
                    PaymentReminderDTO dto = new PaymentReminderDTO();
                    dto.setPaymentId(payment.getId());
                    dto.setAmount(payment.getAmount());
                    dto.setDueDate(payment.getDueDate());
                    dto.setPolicyNumber(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : "N/A");
                    dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "N/A");
                    return dto;
                })
                .collect(Collectors.toList());
    }
}