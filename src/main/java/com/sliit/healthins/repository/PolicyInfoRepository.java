package com.sliit.healthins.repository;

import com.sliit.healthins.model.PolicyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyInfoRepository extends JpaRepository<PolicyInfo, Long> {
    Optional<PolicyInfo> findByCoverageType(String coverageType);
}

