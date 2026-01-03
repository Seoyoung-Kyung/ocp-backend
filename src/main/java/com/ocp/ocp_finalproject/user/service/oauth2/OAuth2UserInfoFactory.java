package com.ocp.ocp_finalproject.user.service.oauth2;

import com.ocp.ocp_finalproject.user.enums.AuthProvider;

import java.util.Map;

/**
 * OAuth 제공자 별 사용자 정보 추출 팩토리 메서드*/
public class OAuth2UserInfoFactory {

    /**
     * 제공자에 맞는 OAuth2UserInfo 구현체 생성
     */
    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuthUserInfo(attributes);
            case NAVER -> new NaverOAuthUserInfo(attributes);
            case KAKAO -> new KakaoOAuthUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        };
    }

    /**
     * 제공자별 사용자 식별 속성명 반환
     */
    public static String getUserNameAttribute(AuthProvider provider){
        return switch (provider){
            case GOOGLE -> "sub";
            case NAVER -> "response";
            case KAKAO -> "id";
            default -> "id";
        };
    }
}
