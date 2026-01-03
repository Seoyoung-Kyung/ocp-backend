package com.ocp.ocp_finalproject.integration.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAiMessage(
        String role,
        String content
) {
    public static OpenAiMessage user(String content) {
        return new OpenAiMessage("user", content);
    }
}
