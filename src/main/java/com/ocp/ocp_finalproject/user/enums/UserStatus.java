package com.ocp.ocp_finalproject.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 계정 상태
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE("활성", "정상 활동 중인 사용자"),
    INACTIVE("비활성", "로그인하지 않은 상태"),
    SUSPENDED("정지", "관리자에 의해 정지됨"),
    DORMANT("휴면", "장기간 미접속으로 휴면 전환"),
    WITHDRAWN("탈퇴", "회원 탈퇴");

    private final String displayName;
    private final String description;

    /**
     * 활성 사용자 여부
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 로그인 가능 여부
     */
    public boolean canLogin() {
        return this == ACTIVE || this == DORMANT;
    }

    /**
     * 서비스 이용 가능 여부
     */
    public boolean canUseService() {
        return this == ACTIVE;
    }

    /**
     * 정지 상태 여부
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    /**
     * 탈퇴 상태 여부
     */
    public boolean isWithdrawn() {
        return this == WITHDRAWN;
    }

    /**
     * 복구 가능 여부
     */
    public boolean canRestore() {
        return this == INACTIVE || this == DORMANT || this == SUSPENDED;
    }
}