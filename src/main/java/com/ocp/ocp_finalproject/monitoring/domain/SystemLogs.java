package com.ocp.ocp_finalproject.monitoring.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.ocp.ocp_finalproject.monitoring.enums.LogLevel;

/**
 * 시스템 로그 엔티티
 * 시스템 전반의 로그 및 에러 기록
 */
@Entity
@Table(name = "system_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemLogs extends BaseEntity {

    /**
     * 로그 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long id;

    /**
     * 로그 레벨 (INFO, WARN, ERROR 등)
     */
    @Column(name = "log_level", length = 10)
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    /**
     * 로그 메시지
     */
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /**
     * 에러 타입
     */
    @Column(name = "error_type", length = 50)
    private String errorType;

    /**
     * 작업 타입
     */
    @Column(name = "task_type", length = 50)
    private String taskType;

    /**
     * IP 주소
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 스택 트레이스
     */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    /**
     * 요청 데이터 (JSON)
     */
    @Column(name = "request_data", columnDefinition = "JSON")
    private String requestData;

    @Builder(builderMethodName = "createBuilder")
    public static SystemLogs create(LogLevel logLevel, String message, String errorType, String taskType,
                                    String ipAddress, String stackTrace, String requestData) {
        SystemLogs systemLogs = new SystemLogs();
        systemLogs.logLevel = logLevel;
        systemLogs.message = message;
        systemLogs.errorType = errorType;
        systemLogs.taskType = taskType;
        systemLogs.ipAddress = ipAddress;
        systemLogs.stackTrace = stackTrace;
        systemLogs.requestData = requestData;
        return systemLogs;
    }
}