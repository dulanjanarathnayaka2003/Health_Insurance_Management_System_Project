package com.sliit.healthins.repository;

import com.sliit.healthins.model.Claim;
import com.sliit.healthins.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimDateBetween(LocalDate start, LocalDate end);
    List<Claim> findByStatus(ClaimStatus status);
    long countByStatus(ClaimStatus status);
    List<Claim> findByPolicyCustomerId(Long customerId);
    List<Claim> findByPolicyCustomerIdAndStatus(Long customerId, ClaimStatus status);
    long countByPolicyCustomerId(Long customerId);
    long countByPolicyCustomerIdAndStatus(Long customerId, ClaimStatus status);
    long countByPolicy_Id(Long policyId);
    long countByPolicy_IdAndStatus(Long policyId, ClaimStatus status);
    java.util.Optional<Claim> findByClaimId(String claimId);
    List<Claim> findByPolicy(com.sliit.healthins.model.Policy policy);
}