package com.sliit.healthins.repository;

import com.sliit.healthins.model.Inquiry;
import com.sliit.healthins.model.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByStatus(InquiryStatus status);
    long countByStatus(InquiryStatus status);
    List<Inquiry> findByCustomerId(Long customerId); // Fixed to use Long and proper naming
    List<?> findByResolutionDateBetween(LocalDate from, LocalDate to);
}