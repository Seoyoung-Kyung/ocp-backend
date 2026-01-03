package com.ocp.ocp_finalproject.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 권한
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {

    ADMIN("관리자", "시스템 전체 관리 권한"),
    USER("사용자", "일반 사용자 권한"),
    GUEST("게스트", "제한된 권한");

    private final String displayName;
    private final String description;

    /**
     * 관리자 여부
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * 일반 사용자 여부
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 게스트 여부
     */
    public boolean isGuest() {
        return this == GUEST;
    }

    /**
     * 전체 접근 권한 여부
     */
    public boolean hasFullAccess() {
        return this == ADMIN;
    }

    /**
     * 관리자 기능 접근 가능 여부
     */
    public boolean canAccessAdminFeatures() {
        return this == ADMIN;
    }
}