package com.ocp.ocp_finalproject.work.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "airflow-log")
public class AirflowLogProperties {
    /**
     * Airflow 로그 수집 웹훅 시크릿 값.
     */
    private String webhookSecret;

    /**
     * Airflow 로그 수집 웹훅 URL.
     */
    private String webhookUrl;
}