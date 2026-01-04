package com.sliit.healthins;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.*;
import com.sliit.healthins.repository.*;
import com.sliit.healthins.service.CustomerSupportService;
import com.sliit.healthins.util.EmailSenderUtil;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerSupportServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private EmailSenderUtil emailSenderUtil;
    @Mock
    private ModelMapper modelMapper;
    @Setter
    @Getter
    @Mock
    private JavaMailSender mailSender; // Mocked for EmailSenderUtil dependency

    @InjectMocks
    private CustomerSupportService customerSupportService;

    public CustomerSupportServiceTest(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configure ModelMapper mock behavior
        when(modelMapper.map(any(), eq(CustomerDTO.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            CustomerDTO dto = new CustomerDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setContact(user.getContact());
            dto.setEmail(user.getEmail());
            dto.setStatus("N/A");
            dto.setPolicyNumber("N/A");
            dto.setCreatedAt(user.getCreatedAt());
            dto.setUpdatedAt(user.getUpdatedAt());
            return dto;
        });
        when(modelMapper.map(any(), eq(PolicyDetailsDTO.class))).thenReturn(new PolicyDetailsDTO());
        when(modelMapper.map(any(), eq(ClaimUpdateDTO.class))).thenReturn(new ClaimUpdateDTO());
        when(modelMapper.map(any(), eq(InquiryDTO.class))).thenAnswer(invocation -> {
            Inquiry inquiry = invocation.getArgument(0);
            return new InquiryDTO(inquiry.getId(), null, null, null, inquiry.getStatus().name(), inquiry.getResolutionDate());
        });
    }

    @Test
    public void testGetDashboardStats_Success() {
        when(userRepository.countByRole(Role.POLICYHOLDER)).thenReturn(100L);
        when(claimRepository.countByStatus(ClaimStatus.PENDING)).thenReturn(50L);
        when(inquiryRepository.countByStatus(InquiryStatus.RESOLVED)).thenReturn(30L);
        when(paymentRepository.countByStatus(PaymentStatus.OVERDUE)).thenReturn(20L);

        DashboardStatsDTO stats = customerSupportService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(100L, stats.getTotalCustomers());
        assertEquals(50L, stats.getPendingClaims());
        assertEquals(30L, stats.getResolvedInquiries());
        assertEquals(20L, stats.getOverduePayments());
        verify(userRepository, times(1)).countByRole(Role.POLICYHOLDER);
        verify(claimRepository, times(1)).countByStatus(ClaimStatus.PENDING);
        verify(inquiryRepository, times(1)).countByStatus(InquiryStatus.RESOLVED);
        verify(paymentRepository, times(1)).countByStatus(PaymentStatus.OVERDUE);
    }

    @Test
    public void testSearchCustomers_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setContact("1234567890");
        user.setEmail("john@example.com");
        user.setCreatedAt(LocalDate.now().atStartOfDay());
        user.setUpdatedAt(LocalDate.now().atStartOfDay());
        when(userRepository.findByNameContainingIgnoreCaseOrContactContainingIgnoreCaseOrEmailContainingIgnoreCase(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(user));

        List<CustomerDTO> customers = customerSupportService.searchCustomers("John");

        assertNotNull(customers);
        assertEquals(1, customers.size());
        assertEquals("John Doe", customers.getFirst().getName());
        verify(userRepository, times(1)).findByNameContainingIgnoreCaseOrContactContainingIgnoreCaseOrEmailContainingIgnoreCase(anyString(), anyString(), anyString());
    }

    @Test
    public void testSearchCustomers_EmptyQuery() {
        assertThrows(IllegalArgumentException.class, () -> customerSupportService.searchCustomers(""));
    }

    @Test
    public void testUpdateCustomer_Success() {
        User user = new User();
        user.setId(1L);
        CustomerDTO dto = new CustomerDTO();
        dto.setName("Jane Doe");
        dto.setContact("0987654321");
        dto.setEmail("jane@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        CustomerDTO result = customerSupportService.updateCustomer(1L, dto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerSupportService.updateCustomer(1L, new CustomerDTO()));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdatePolicy_Success() {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL123");
        PolicyDetailsDTO dto = new PolicyDetailsDTO();
        when(policyRepository.findByPolicyNumber("POL123")).thenReturn(Optional.of(policy));
        when(policyRepository.save(policy)).thenReturn(policy);

        PolicyDetailsDTO result = customerSupportService.updatePolicy("POL123", dto);

        assertNotNull(result);
        verify(policyRepository, times(1)).findByPolicyNumber("POL123");
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    public void testUpdateClaimStatus_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        ClaimUpdateDTO dto = new ClaimUpdateDTO();
        dto.setStatus("APPROVED");
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(claim)).thenReturn(claim);

        ClaimUpdateDTO result = customerSupportService.updateClaimStatus(1L, dto);

        assertNotNull(result);
        assertEquals(ClaimStatus.APPROVED, claim.getStatus());
        verify(claimRepository, times(1)).findById(1L);
        verify(claimRepository, times(1)).save(claim);
    }

    @Test
    public void testGetClaims_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        when(claimRepository.findAll()).thenReturn(Collections.singletonList(claim));

        List<ClaimUpdateDTO> claims = customerSupportService.getClaims();

        assertNotNull(claims);
        assertEquals(1, claims.size());
        verify(claimRepository, times(1)).findAll();
    }

    @Test
    public void testGenerateReport_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setStatus(ClaimStatus.PENDING);
        when(claimRepository.findByClaimDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(claim));

        byte[] report = customerSupportService.generateReport("claims", "2025-10-01", "2025-10-01");

        assertNotNull(report);
        assertTrue(report.length > 0);
        verify(claimRepository, times(1)).findByClaimDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testGenerateReport_InvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> customerSupportService.generateReport("claims", "invalid", "2025-10-01"));
    }

    @Test
    public void testGetPendingInquiries_Success() {
        Inquiry inquiry = new Inquiry();
        inquiry.setId(1L);
        inquiry.setStatus(InquiryStatus.OPEN);
        when(inquiryRepository.findByStatus(InquiryStatus.OPEN)).thenReturn(Collections.singletonList(inquiry));

        List<InquiryDTO> pendingInquiries = customerSupportService.getPendingInquiries();

        assertNotNull(pendingInquiries);
        assertEquals(1, pendingInquiries.size());
        assertEquals("OPEN", pendingInquiries.getFirst().getStatus());
        verify(inquiryRepository, times(1)).findByStatus(InquiryStatus.OPEN);
    }

    @Test
    public void testUpdateInquiry_Success() {
        Inquiry inquiry = new Inquiry();
        inquiry.setId(1L);
        inquiry.setStatus(InquiryStatus.OPEN);
        InquiryDTO dto = new InquiryDTO(1L, null, null, null, "RESOLVED", LocalDate.now());
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.save(inquiry)).thenReturn(inquiry);
        when(modelMapper.map(inquiry, InquiryDTO.class)).thenReturn(dto);

        InquiryDTO result = customerSupportService.updateInquiry(1L, dto);

        assertNotNull(result);
        assertEquals("RESOLVED", result.getStatus());
        verify(inquiryRepository, times(1)).findById(1L);
        verify(inquiryRepository, times(1)).save(inquiry);
    }

    @Test
    public void testUpdateInquiry_NotFound() {
        when(inquiryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerSupportService.updateInquiry(1L, new InquiryDTO()));
        verify(inquiryRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateInquiry_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> customerSupportService.updateInquiry(1L, notNull()));
    }

    @Test
    public void testSendPaymentReminder_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        User customer = new User();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        payment.setCustomer(customer);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setDueDate(LocalDate.now());
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentReminderDTO reminderDTO = new PaymentReminderDTO();
        reminderDTO.setPaymentId(1L);
        reminderDTO.setReminderType("GENTLE");

        customerSupportService.sendPaymentReminder(reminderDTO);

        verify(paymentRepository, times(1)).findById(1L);
        verify(emailSenderUtil, times(1)).sendEmail(eq("john@example.com"), startsWith("Payment Reminder - GENTLE"), anyString());
    }

    @Test
    public void testSendPaymentReminder_NotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerSupportService.sendPaymentReminder(new PaymentReminderDTO()));
        verify(paymentRepository, times(1)).findById(1L);
    }

}