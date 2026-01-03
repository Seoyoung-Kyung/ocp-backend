package com.ocp.ocp_finalproject.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 실행 상태 (공통)
 *
 * 사용 대상:
 * - HtmlCrawl (HTML 추출 작업)
 * - ProductCrawl (상품 크롤링 작업)
 */
@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {

    PENDING("대기", "실행 대기 중"),
    RUNNING("실행중", "실행 중"),
    COMPLETED("완료", "실행 완료"),
    FAILED("실패", "실행 실패");

    private final String displayName;
    private final String description;

    /**
     * 진행 중 여부
     */
    public boolean isInProgress() {
        return this == PENDING || this == RUNNING;
    }

    /**
     * 종료 여부
     */
    public boolean isFinished() {
        return this == COMPLETED || this == FAILED;
    }

    /**
     * 성공 여부
     */
    public boolean isSuccess() {
        return this == COMPLETED;
    }

    /**
     * 실패 여부
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * 재시도 가능 여부
     */
    public boolean canRetry() {
        return this == FAILED;
    }
}