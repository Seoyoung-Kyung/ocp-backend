package com.ocp.ocp_finalproject.user.service.oauth2;

public interface OAuth2UserInfo {
    /**
     * 제공자의 사용자 고유 아이디
     */
    String getProviderId();

    /**
     * 사용자 이메일
     */
    String getEmail();

    /**
     * 사용자 이름
     */
    String getName();
}
