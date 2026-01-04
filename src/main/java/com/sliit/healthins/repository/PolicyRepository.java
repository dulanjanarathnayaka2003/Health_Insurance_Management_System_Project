package com.sliit.healthins.repository;

import com.sliit.healthins.model.Policy;
import com.sliit.healthins.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByEndDateBeforeAndStatus(LocalDate date, PolicyStatus status);
    List<Policy> findByCustomerId(Long customerId);
    Optional<Policy> findByPolicyNumber(String policyNumber);
    long countByCustomerIdAndStatus(Long customerId, PolicyStatus status);
}