package com.ocp.ocp_finalproject.monitoring.repository;

import com.ocp.ocp_finalproject.monitoring.domain.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
}
