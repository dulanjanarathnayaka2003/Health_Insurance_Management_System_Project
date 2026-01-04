package com.sliit.healthins;

import com.sliit.healthins.dto.ContactUpdateDTO;
import com.sliit.healthins.dto.ProfileDTO;
import com.sliit.healthins.model.*;
import com.sliit.healthins.repository.ClaimRepository;
import com.sliit.healthins.repository.InquiryRepository;
import com.sliit.healthins.repository.PaymentRepository;
import com.sliit.healthins.service.CustomerPortalService;
import com.sliit.healthins.util.EmailSenderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerPortalServiceTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private EmailSenderUtil emailUtil;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerPortalService customerPortalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitClaim_Success() {
        Claim claim = new Claim();
        claim.setPolicyId("POL123");
        claim.setDescription("Medical Claim");
        claim.setStatus(ClaimStatus.PENDING);

        when(claimRepository.save(claim)).thenReturn(claim);

        Claim submittedClaim = customerPortalService.submitClaim(claim);

        assertNotNull(submittedClaim);
        assertEquals("POL123", submittedClaim.getPolicyId());
        verify(claimRepository, times(1)).save(claim);
    }

    @Test
    public void testGetPaymentStatus_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPolicyId(String.valueOf(123L)); // Use Long if policyId is numeric
        payment.setStatus(PaymentStatus.PAID);

        List<Payment> payments = Collections.singletonList(payment);
        when(paymentRepository.findByPolicy_Id(123L)).thenReturn(payments); // Match type

        List<Payment> paymentStatus = customerPortalService.getPaymentStatus(123L);

        assertNotNull(paymentStatus);
        assertEquals(1, paymentStatus.size());
        verify(paymentRepository, times(1)).findByPolicy_Id(123L);
    }

    @Test
    public void testGetInquiryHistory_Success() {
        Inquiry inquiry = new Inquiry();
        inquiry.setId(1L);
        inquiry.setSubject("Billing Issue");
        inquiry.setStatus(InquiryStatus.RESOLVED);

        List<Inquiry> inquiries = Collections.singletonList(inquiry);
        when(inquiryRepository.findByCustomerId(1L)).thenReturn(inquiries);

        List<Inquiry> inquiryHistory = customerPortalService.getInquiryHistory(1L);

        assertNotNull(inquiryHistory);
        assertEquals(1, inquiryHistory.size());
        verify(inquiryRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    public void testUpdateContact_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@example.com");
        when(claimRepository.findById(1L)).thenReturn(Optional.empty()); // Mock claim not needed
        when(paymentRepository.findByPolicyCustomerId(1L)).thenReturn(Collections.emptyList());
        when(inquiryRepository.findByCustomerId(1L)).thenReturn(Collections.emptyList());
        customerPortalService.updateContact(1L, new ContactUpdateDTO("new@example.com", null));

        verify(paymentRepository, times(1)).findByPolicyCustomerId(1L);
        verify(inquiryRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    public void testGetProfile_Success() {
        User user = new User();
        user.setId(1L);
        when(modelMapper.map(user, ProfileDTO.class)).thenReturn(new ProfileDTO());
        // Mock repository logic as needed

        ProfileDTO profile = customerPortalService.getProfile(1L);

        assertNotNull(profile);
        verify(modelMapper, times(1)).map(user, ProfileDTO.class);
    }
}