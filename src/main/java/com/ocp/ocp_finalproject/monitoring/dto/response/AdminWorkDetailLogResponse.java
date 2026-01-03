package com.ocp.ocp_finalproject.monitoring.dto.response;

import com.ocp.ocp_finalproject.monitoring.enums.LogLevel;
import com.ocp.ocp_finalproject.monitoring.enums.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWorkDetailLogResponse {
    private Long logId;
    private Integer stepNumber;
    private String stepName;
    private String logData;        // 전체 로그 원본
    private StepStatus status;
    private LogLevel logLevel;
    private LocalDateTime createdAt;
}
