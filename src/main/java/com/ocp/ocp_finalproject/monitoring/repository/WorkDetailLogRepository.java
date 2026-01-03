package com.ocp.ocp_finalproject.monitoring.repository;

import com.ocp.ocp_finalproject.monitoring.domain.WorkDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkDetailLogRepository extends JpaRepository<WorkDetailLog, Long> {

    // workId로 로그 조회 (stepNumber 오름차순)
    List<WorkDetailLog> findByWorkIdOrderByStepNumberAsc(Long workId);

    // workId + stepNumber로 조회
    List<WorkDetailLog> findByWorkIdAndStepNumberOrderByIdAsc(Long workId, Integer stepNumber);

    // workId 삭제
    void deleteByWorkId(Long workId);
}
