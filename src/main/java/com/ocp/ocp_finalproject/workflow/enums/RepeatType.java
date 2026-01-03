package com.ocp.ocp_finalproject.workflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 반복 유형
 */
@Getter
@RequiredArgsConstructor
public enum RepeatType {

    ONCE("한번만", "1회만 실행"),
    DAILY("매일", "매일 반복 실행"),
    WEEKLY("매주", "매주 반복 실행"),
    MONTHLY("매월", "매월 반복 실행"),
    CUSTOM("사용자정의", "사용자가 정의한 반복 규칙");

    private final String displayName;
    private final String description;

    /**
     * 반복 실행 여부
     */
    public boolean isRepeating() {
        return this != ONCE;
    }

    /**
     * 스케줄 필요 여부
     */
    public boolean needsSchedule() {
        return this != ONCE;
    }
}