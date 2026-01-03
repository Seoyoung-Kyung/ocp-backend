package com.ocp.ocp_finalproject.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 인증 제공자 (OAuth)
 */
@Getter
@RequiredArgsConstructor
public enum AuthProvider {

    LOCAL("로컬", "이메일/비밀번호 로그인"),
    KAKAO("카카오", "카카오 계정 로그인"),
    GOOGLE("구글", "구글 계정 로그인"),
    NAVER("네이버", "네이버 계정 로그인");

    private final String displayName;
    private final String description;

    /**
     * OAuth 제공자 여부
     */
    public boolean isOAuth() {
        return this != LOCAL;
    }

    /**
     * 로컬 로그인 여부
     */
    public boolean isLocal() {
        return this == LOCAL;
    }

    /**
     * 소셜 로그인 여부
     */
    public boolean isSocial() {
        return this != LOCAL;
    }
}