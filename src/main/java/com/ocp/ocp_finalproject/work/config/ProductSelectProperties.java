package com.ocp.ocp_finalproject.work.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "product-select")
public class ProductSelectProperties {

    private String webhookSecret;
    private String webhookUrl;
}
