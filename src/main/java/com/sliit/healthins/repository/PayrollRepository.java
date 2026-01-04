package com.sliit.healthins.repository;

import com.sliit.healthins.model.Payroll;
import com.sliit.healthins.model.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByDateBetween(LocalDate start, LocalDate end);
    long countByStatus(PayrollStatus status);
    List<Payroll> findByEmployeeId(Long employeeId);
}