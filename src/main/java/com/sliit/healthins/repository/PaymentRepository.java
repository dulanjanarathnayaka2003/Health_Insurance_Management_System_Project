package com.sliit.healthins.repository;
import com.sliit.healthins.model.Payment;
import com.sliit.healthins.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDueDateBeforeAndStatus(LocalDate date, PaymentStatus status);
    long countByStatus(PaymentStatus status);
    List<Payment> findByPolicyCustomerId(Long customerId);
    List<Payment> findByPolicy_Id(Long policyId);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Optional<Payment> findFirstByPolicyCustomerIdAndStatusOrderByDueDateAsc(Long customerId, PaymentStatus status);
}