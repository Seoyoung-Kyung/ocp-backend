package com.ocp.ocp_finalproject.integration.openai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    /**
     * OpenAI API Key
     */
    private String apiKey;

    /**
     * ex) https://api.openai.com/v1
     */
    private String baseUrl = "https://api.openai.com/v1";

    /**
     * 기본 모델명 (지정하지 않으면 호출 불가)
     */
    private String defaultModel = "gpt-4o-mini";

    /**
     * 필요 시 조직 헤더
     */
    private String organization;
}
