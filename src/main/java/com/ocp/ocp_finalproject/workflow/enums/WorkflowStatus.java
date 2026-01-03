package com.ocp.ocp_finalproject.workflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 워크플로우 상태
 */
@Getter
@RequiredArgsConstructor
public enum WorkflowStatus {

    PRE_REGISTERED("등록전", "워크플로우가 아직 등록되지 않은 상태"),
    PENDING("대기", "워크플로우 생성 후 첫 실행 대기 중"),
    ACTIVE("활성", "워크플로우가 활성화되어 자동 실행 중"),
    INACTIVE("비활성", "워크플로우가 비활성화되어 실행 중지"),
    COMPLETED("완료", "워크플로우 완전 종료"),
    DELETED("삭제", "워크플로우 삭제됨");

    private final String displayName;
    private final String description;

    /**
     * 활성 상태 여부
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 실행 가능 여부
     */
    public boolean canExecute() {
        return this == ACTIVE;
    }

    /**
     * 종료 상태 여부
     */
    public boolean isTerminated() {
        return this == COMPLETED || this == DELETED;
    }

    /**
     * 활성화 가능 여부
     */
    public boolean canActivate() {
        return this == PENDING || this == INACTIVE;
    }

    /**
     * 비활성화 가능 여부
     */
    public boolean canDeactivate() {
        return this == ACTIVE;
    }

    /**
     * 상태 전이 가능 여부 체크
     */
    public boolean canTransitionTo(WorkflowStatus newStatus) {
        return switch (this) {
            case PRE_REGISTERED -> newStatus == PENDING
                    || newStatus == DELETED;

            case PENDING -> newStatus == INACTIVE
                    || newStatus == DELETED
                    || newStatus == ACTIVE
                    || newStatus == PENDING;

            case ACTIVE ->
                    newStatus == INACTIVE
                            || newStatus == COMPLETED
                            || newStatus == DELETED
                            || newStatus == PENDING;

            case INACTIVE ->
                    newStatus == ACTIVE
                            || newStatus == COMPLETED
                            || newStatus == DELETED;

            case COMPLETED ->
                    newStatus == DELETED;

            case DELETED ->
                    false;
        };
    }

}