package com.ocp.ocp_finalproject.user.service.oauth2;

import java.util.Map;

public class KakaoOAuthUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String,Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        Map<String,Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String,Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("nickname");
    }
}
