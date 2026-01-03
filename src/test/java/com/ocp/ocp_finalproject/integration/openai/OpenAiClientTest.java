package com.ocp.ocp_finalproject.integration.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiMessage;
import com.ocp.ocp_finalproject.monitoring.domain.AiUsageLog;
import com.ocp.ocp_finalproject.monitoring.enums.AiFeatureType;
import com.ocp.ocp_finalproject.monitoring.repository.AiUsageLogRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@TestPropertySource(properties = {
        "openai.api-key=test-api-key",
        "openai.base-url=https://api.openai.com/v1",
        "openai.default-model=gpt-4o-mini",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver"
})
class OpenAiClientTest {

    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private AiUsageLogRepository aiUsageLogRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.verify();
        }
        aiUsageLogRepository.deleteAll();
    }

    @Test
    void chatCompletionPersistsUsageLog() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        String sampleResponse = """
                {
                  "id": "chatcmpl-test",
                  "model": "gpt-4o-mini",
                  "choices": [
                    {
                      "index": 0,
                      "message": {
                        "role": "assistant",
                        "content": "hello!"
                      },
                      "finish_reason": "stop"
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 12,
                    "completion_tokens": 24,
                    "total_tokens": 36
                  }
                }
                """;

        mockServer.expect(ExpectedCount.once(), requestTo(OPENAI_ENDPOINT))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-api-key"))
                .andRespond(withSuccess(sampleResponse, MediaType.APPLICATION_JSON));

        openAiClient.chatCompletion(
                AiFeatureType.CONTENT_GENERATE,
                1L,
                10L,
                List.of(new OpenAiMessage("user", "ping")),
                "gpt-4o-mini"
        );

        List<AiUsageLog> logs = aiUsageLogRepository.findAll();
        assertThat(logs).hasSize(1);
        AiUsageLog log = logs.get(0);
        assertThat(log.getFeatureType()).isEqualTo(AiFeatureType.CONTENT_GENERATE.name());
        assertThat(log.getModel()).isEqualTo("gpt-4o-mini");
        assertThat(log.getPromptTokens()).isEqualTo(12);
        assertThat(log.getCompletionTokens()).isEqualTo(24);
        assertThat(log.getTotalTokens()).isEqualTo(36);
        assertThat(log.getEstimatedCost()).isNotNull();
    }
}
