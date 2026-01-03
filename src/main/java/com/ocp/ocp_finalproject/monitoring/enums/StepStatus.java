package com.ocp.ocp_finalproject.monitoring.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 단계 실행 상태
 * WorkDetailLog의 각 단계별 실행 결과
 */
@Getter
@RequiredArgsConstructor
public enum StepStatus {

    SUCCESS("성공", "단계 실행 성공"),
    FAILED("실패", "단계 실행 실패"),
    SKIPPED("건너뜀", "단계 건너뜀"),
    RETRY("재시도", "재시도 중");

    private final String displayName;
    private final String description;

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isFailed() {
        return this == FAILED;
    }
}