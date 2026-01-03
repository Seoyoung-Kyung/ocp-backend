package com.ocp.ocp_finalproject.integration.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAiChatResponse(
        String id,
        String model,
        OpenAiUsage usage
) {
}
