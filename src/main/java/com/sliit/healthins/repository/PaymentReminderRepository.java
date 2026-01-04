package com.sliit.healthins.repository;

import com.sliit.healthins.model.PaymentReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Long> {
    List<PaymentReminder> findByScheduleDateBeforeAndSentFalse(LocalDate date);
}