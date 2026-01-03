package com.ocp.ocp_finalproject.content.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 콘텐츠 생성 상태
 */
@Getter
@RequiredArgsConstructor
public enum ContentStatus {

    PENDING("대기", "AI 생성 대기 중"),
    GENERATING("생성중", "AI가 콘텐츠 생성 중"),
    GENERATED("생성완료", "AI 콘텐츠 생성 완료"),
    APPROVED("승인", "사용자가 콘텐츠 승인"),
    REJECTED("거부", "사용자가 콘텐츠 거부"),
    PUBLISHED("발행완료", "블로그에 발행 완료"),
    FAILED("실패", "AI 콘텐츠 생성 실패");

    private final String displayName;
    private final String description;

    /**
     * 생성 중 여부
     */
    public boolean isGenerating() {
        return this == GENERATING;
    }

    /**
     * 생성 완료 여부
     */
    public boolean isGenerated() {
        return this == GENERATED || this == APPROVED || this == PUBLISHED;
    }

    /**
     * 편집 가능 여부
     */
    public boolean canEdit() {
        return this == GENERATED || this == REJECTED;
    }

    /**
     * 승인 가능 여부
     */
    public boolean canApprove() {
        return this == GENERATED;
    }

    /**
     * 거부 가능 여부
     */
    public boolean canReject() {
        return this == GENERATED;
    }

    /**
     * 발행 가능 여부
     */
    public boolean canPublish() {
        return this == APPROVED;
    }

    /**
     * 재생성 가능 여부
     */
    public boolean canRegenerate() {
        return this == REJECTED || this == FAILED;
    }

    /**
     * 최종 상태 여부
     */
    public boolean isFinalized() {
        return this == PUBLISHED || this == FAILED;
    }
}