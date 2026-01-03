package com.ocp.ocp_finalproject.user.service.oauth2;

import java.util.Map;

public class GoogleOAuthUserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;

    public GoogleOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
