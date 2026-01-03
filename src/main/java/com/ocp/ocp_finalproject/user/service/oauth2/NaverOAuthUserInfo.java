package com.ocp.ocp_finalproject.user.service.oauth2;

import java.util.Map;

public class NaverOAuthUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public NaverOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId(){
        Map<String,Object> map = (Map<String, Object>) attributes.get("response");
        return (String) map.get("id");
    }

    @Override
    public String getEmail(){
        Map<String,Object> map = (Map<String, Object>) attributes.get("response");
        return (String) map.get("email");
    }

    @Override
    public String getName(){
        Map<String,Object> map = (Map<String, Object>) attributes.get("response");
        return (String) map.get("name");
    }
}
