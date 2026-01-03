package com.ocp.ocp_finalproject.integration.openai;

import com.ocp.ocp_finalproject.integration.openai.config.OpenAiProperties;
import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiChatRequest;
import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiChatResponse;
import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiMessage;
import com.ocp.ocp_finalproject.monitoring.enums.AiFeatureType;
import com.ocp.ocp_finalproject.monitoring.service.AiUsageLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final RestTemplate restTemplate;
    private final OpenAiProperties openAiProperties;
    private final AiUsageLogService aiUsageLogService;

    public OpenAiChatResponse chatCompletion(AiFeatureType featureType,
                                             Long workId,
                                             Long userId,
                                             List<OpenAiMessage> messages,
                                             String model) {
        Assert.notEmpty(messages, "messages must not be empty");
        String resolvedModel = resolveModel(model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(requireApiKey());
        if (StringUtils.hasText(openAiProperties.getOrganization())) {
            headers.add("OpenAI-Organization", openAiProperties.getOrganization());
        }

        OpenAiChatRequest requestBody = OpenAiChatRequest.of(resolvedModel, messages);

        try {
            HttpEntity<OpenAiChatRequest> httpEntity = new HttpEntity<>(requestBody, headers);
            OpenAiChatResponse response = restTemplate.postForObject(
                    openAiProperties.getBaseUrl() + CHAT_COMPLETIONS_PATH,
                    httpEntity,
                    OpenAiChatResponse.class
            );

            if (response == null) {
                log.warn("OpenAI 응답이 비어있습니다. featureType={}, workId={}, userId={}",
                        featureType, workId, userId);
                return null;
            }

            aiUsageLogService.saveUsage(featureType,
                    response.model() != null ? response.model() : resolvedModel,
                    response.usage(),
                    workId,
                    userId);
            return response;
        } catch (RestClientException ex) {
            log.error("OpenAI 호출 중 오류가 발생했습니다. featureType={}, workId={}, userId={}, message={}",
                    featureType, workId, userId, ex.getMessage());
            throw ex;
        }
    }

    private String resolveModel(String model) {
        if (StringUtils.hasText(model)) {
            return model;
        }
        if (!StringUtils.hasText(openAiProperties.getDefaultModel())) {
            throw new IllegalStateException("OpenAI 기본 모델이 설정되지 않았습니다.");
        }
        return openAiProperties.getDefaultModel();
    }

    private String requireApiKey() {
        if (!StringUtils.hasText(openAiProperties.getApiKey())) {
            throw new IllegalStateException("OpenAI API 키가 설정되지 않았습니다.");
        }
        return openAiProperties.getApiKey();
    }
}
