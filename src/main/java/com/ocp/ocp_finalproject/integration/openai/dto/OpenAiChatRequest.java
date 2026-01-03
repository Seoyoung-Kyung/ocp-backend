package com.ocp.ocp_finalproject.integration.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAiChatRequest(
        String model,
        List<OpenAiMessage> messages
) {
    public static OpenAiChatRequest of(String model, List<OpenAiMessage> messages) {
        return new OpenAiChatRequest(model, messages);
    }
}
