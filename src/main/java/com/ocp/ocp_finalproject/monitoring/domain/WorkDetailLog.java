package com.ocp.ocp_finalproject.monitoring.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.monitoring.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;
import com.ocp.ocp_finalproject.monitoring.enums.LogLevel;

/**
 * 작업 상세 로그 엔티티
 * 작업(Work) 실행 단계별 로그 기록
 */
@Entity
@Table(name = "work_detail_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkDetailLog extends BaseEntity {

    /**
     * 로그 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long id;

    /**
     * 작업 ID (Foreign Key)
     */
    @Column(name = "work_id", nullable = false)
    private Long workId;

    /**
     * 단계 번호
     */
    @Column(name = "step_number")
    private Integer stepNumber;

    /**
     * 단계 이름
     */
    @Column(name = "step_name", length = 100)
    private String stepName;

    /**
     * 로그 데이터
     */
    @Column(name = "log_data", columnDefinition = "TEXT")
    private String logData;

    /**
     * 상태
     */
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private StepStatus status;

    /**
     * 로그 레벨
     */
    @Column(name = "log_level", length = 100)
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    @Builder(builderMethodName = "createBuilder")
    public static WorkDetailLog create(Long workId, Integer stepNumber, String stepName, String logData, StepStatus status, LogLevel logLevel) {
        WorkDetailLog workDetailLog = new WorkDetailLog();
        workDetailLog.workId = workId;
        workDetailLog.stepNumber = stepNumber;
        workDetailLog.stepName = stepName;
        workDetailLog.logData = logData;
        workDetailLog.status = status;
        workDetailLog.logLevel = logLevel;
        return workDetailLog;
    }
}