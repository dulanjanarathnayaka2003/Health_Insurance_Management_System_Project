package com.sliit.healthins.repository;

import com.sliit.healthins.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByUserId(Long userId); // Assuming user_id is mapped correctly
    AuditLog findTopByOrderByTimestampDesc(); // Fixed return type to AuditLog
}