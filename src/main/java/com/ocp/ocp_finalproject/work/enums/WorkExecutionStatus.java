package com.ocp.ocp_finalproject.work.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * work 실행 상태 (공통)
 *
 * 사용 대상:
 * - Work (작업 실행)
 */
@Getter
@RequiredArgsConstructor
public enum WorkExecutionStatus {

    PENDING("대기", "실행 대기 중"),
    REQUESTED("요청됨", "AI 처리 요청 전송 완료"),
    TREND_KEYWORD_DONE("키워드 추출 성공", "트렌드 키워드 추출 완료"),
    PRODUCT_SELECTED("상품 선택 완료", "상품 선택 완료"),
    CONTENT_GENERATED("콘텐츠 생성 완료", "콘텐츠 생성 완료"),
    BLOG_UPLOAD_PENDING("블로그 업로드 준비", "블로그 업로드 큐 대기"),
    COMPLETED("완료", "전체 파이프라인 완료"),
    FAILED("실패", "작업 실패");

    private final String displayName;
    private final String description;

    /**
     * 진행 중 여부
     */
    public boolean isInProgress() {
        return this == PENDING
                || this == REQUESTED
                || this == TREND_KEYWORD_DONE
                || this == PRODUCT_SELECTED
                || this == CONTENT_GENERATED
                || this == BLOG_UPLOAD_PENDING;
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
