package com.ocp.ocp_finalproject.work.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keyword-select")
public class KeywordSelectProperties {
    /**
     * 워커가 웹훅을 호출할 때 Authorization 헤더로 사용할 시크릿 값.
     */
    private String webhookSecret;

    /**
     * 워커가 결과를 전송할 기본 웹훅 URL.
     */
    private String webhookUrl;
}
