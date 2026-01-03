package com.ocp.ocp_finalproject.common.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OAuth2 관련 설정 Properties
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix="oauth2.redirect")
public class OAuth2Properties {
    /**
     * 로그인 성공 시 리다이렉트 URL
     */
    private String successUrl;

    /**
     * 로그인 실패 시 리다이렉트 URL
     */
    private String failureUrl;
}
