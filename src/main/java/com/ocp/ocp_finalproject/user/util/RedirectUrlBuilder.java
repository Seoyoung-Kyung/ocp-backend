package com.ocp.ocp_finalproject.user.util;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * 리다이렉트 URL 생성 유틸리티
 */
public class RedirectUrlBuilder {

    /***
     * 로그인 성공 리다이렉트 URL 생성
     * @param baseUrl 기본 URL
     * @param userId 사용자 ID
     * @param isNewUser 신규 가입 여부
     * @return 쿼리 파라미터가 추가된 리다이렉트 URL
     */
    public static String buildSuccessUrl(String baseUrl, Long userId, boolean isNewUser) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("userId", userId)
                .queryParam("isNewUser", isNewUser)
                .build()
                .toUriString();
    }

    /***
     * 로그인 실패 리다이렉트 URL 생성
     * @param baseUrl 기본 URL
     * @param errorMessage 에러메세지
     * @return 에러 메세지가 추가된 리다이렉트 URL
     */
    public static String buildFailureUrl(String baseUrl, String errorMessage) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("errorMessage", errorMessage)
                .build()
                .toUriString();
    }
}
