package com.sliit.healthins;

import com.sliit.healthins.dto.ClaimDTO;
import com.sliit.healthins.dto.ClaimUpdateDTO;
import com.sliit.healthins.exception.ResourceNotFoundException;
import com.sliit.healthins.model.Claim;
import com.sliit.healthins.model.ClaimStatus;
import com.sliit.healthins.repository.ClaimRepository;
import com.sliit.healthins.repository.PolicyRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.ClaimsService;
import com.sliit.healthins.util.EmailSenderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClaimsServiceTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailSenderUtil emailUtil;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClaimsService claimsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitClaim_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimId("CLM123");
        claim.setStatus(ClaimStatus.PENDING);
        claim.setClaimDate(java.time.LocalDate.now());
        claim.setNotes("Medical Claim");

        when(claimRepository.save(claim)).thenReturn(claim);

        Claim submittedClaim = claimsService.submitClaim(claim);

        assertNotNull(submittedClaim);
        assertEquals("CLM123", submittedClaim.getClaimId());
        verify(claimRepository, times(1)).save(claim);
        verify(emailUtil, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateClaimStatus_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setStatus(ClaimStatus.PENDING);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(claim)).thenReturn(claim);

        ClaimUpdateDTO updateDTO = new ClaimUpdateDTO();
        updateDTO.setStatus("APPROVED");
        
        Claim updatedClaim = claimsService.updateClaimStatus(1L, updateDTO);

        assertNotNull(updatedClaim);
        assertEquals(ClaimStatus.APPROVED, updatedClaim.getStatus());
        verify(claimRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateClaimStatus_NotFound() {
        when(claimRepository.findById(1L)).thenReturn(Optional.empty());

        ClaimUpdateDTO updateDTO = new ClaimUpdateDTO();
        updateDTO.setStatus("APPROVED");
        
        assertThrows(ResourceNotFoundException.class, () -> claimsService.updateClaimStatus(1L, updateDTO));
        verify(claimRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateClaimStatus_InvalidTransition() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setStatus(ClaimStatus.APPROVED);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        ClaimUpdateDTO updateDTO = new ClaimUpdateDTO();
        updateDTO.setStatus("REJECTED");
        
        assertThrows(IllegalStateException.class, () -> claimsService.updateClaimStatus(1L, updateDTO));
        verify(claimRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllClaims_Success() {
        Claim claim = new Claim();
        claim.setId(1L);
        claim.setClaimId("CLM123");
        claim.setStatus(ClaimStatus.PENDING);
        
        when(claimRepository.findAll()).thenReturn(List.of(claim));
        when(modelMapper.map(any(), eq(ClaimDTO.class))).thenReturn(new ClaimDTO());

        List<ClaimDTO> claims = claimsService.getAllClaims();

        assertNotNull(claims);
        assertEquals(1, claims.size());
        verify(claimRepository, times(1)).findAll();
    }

    @Test
    public void testGenerateReport_Success() {
        when(claimRepository.findByClaimDateBetween(any(), any())).thenReturn(List.of());
        byte[] report = claimsService.generateReport(LocalDate.now().minusDays(1), LocalDate.now());
        assertNotNull(report);
        verify(claimRepository, times(1)).findByClaimDateBetween(any(), any());
    }
}