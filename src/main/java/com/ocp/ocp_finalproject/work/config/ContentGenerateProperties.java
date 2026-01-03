package com.ocp.ocp_finalproject.work.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "content-generate")
public class ContentGenerateProperties {

    private String webhookSecret;
    private String webhookUrl;
}
