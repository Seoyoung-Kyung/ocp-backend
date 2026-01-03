package com.ocp.ocp_finalproject.monitoring.service;

import com.ocp.ocp_finalproject.monitoring.domain.WorkDetailLog;
import com.ocp.ocp_finalproject.monitoring.dto.response.AdminWorkDetailLogResponse;
import com.ocp.ocp_finalproject.monitoring.dto.response.WorkDetailLogResponse;
import com.ocp.ocp_finalproject.monitoring.repository.WorkDetailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkDetailLogService {

    private final WorkDetailLogRepository logRepository;

    /**
     * 사용자용 로그 조회
     */
    public List<WorkDetailLogResponse> getUserLogs(Long workId) {
        List<WorkDetailLog> logs = logRepository.findByWorkIdOrderByStepNumberAsc(workId);

        return logs.stream()
                .map(log -> WorkDetailLogResponse.builder()
                        .stepName(log.getStepName())
                        .messages(splitLogData(log.getLogData()))
                        .status(log.getStatus().getDisplayName())
                        .timestamp(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 로그 조회
     */
    public List<AdminWorkDetailLogResponse> getAdminLogs(Long workId) {
        List<WorkDetailLog> logs = logRepository.findByWorkIdOrderByStepNumberAsc(workId);

        return logs.stream()
                .map(log -> AdminWorkDetailLogResponse.builder()
                        .logId(log.getId())
                        .stepNumber(log.getStepNumber())
                        .stepName(log.getStepName())
                        .logData(log.getLogData())
                        .status(log.getStatus())
                        .logLevel(log.getLogLevel())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 로그 데이터를 개행문자로 split하여 List로 변환
     */
    private List<String> splitLogData(String logData) {
        if (logData == null || logData.isBlank()) {
            return List.of();
        }
        return Arrays.stream(logData.split("\n"))
                .filter(line -> !line.isBlank())
                .collect(Collectors.toList());
    }
}