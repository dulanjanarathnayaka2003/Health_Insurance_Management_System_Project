package com.sliit.healthins.repository;

import com.sliit.healthins.config.SystemConfig; // Assuming this exists in config package
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    Optional<SystemConfig> findByConfigName(String configName);
}