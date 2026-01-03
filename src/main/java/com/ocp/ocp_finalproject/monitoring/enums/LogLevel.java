package com.ocp.ocp_finalproject.monitoring.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그 레벨
 */
@Getter
@RequiredArgsConstructor
public enum LogLevel {

    TRACE("추적", "가장 상세한 추적 정보"),
    DEBUG("디버그", "디버깅용 상세 정보"),
    INFO("정보", "일반 정보성 메시지"),
    WARN("경고", "경고 메시지"),
    ERROR("오류", "오류 메시지"),
    FATAL("치명적", "치명적인 오류");

    private final String displayName;
    private final String description;

    /**
     * 에러 레벨 여부
     */
    public boolean isError() {
        return this == ERROR || this == FATAL;
    }

    /**
     * 경고 이상 레벨 여부
     */
    public boolean isWarningOrHigher() {
        return this == WARN || this == ERROR || this == FATAL;
    }

    /**
     * 디버그 레벨 여부
     */
    public boolean isDebug() {
        return this == DEBUG || this == TRACE;
    }
}