package com.ocp.ocp_finalproject.integration.openai;

import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiMessage;
import com.ocp.ocp_finalproject.monitoring.enums.AiFeatureType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 간단한 예시 엔드포인트:
 * - OpenAI 호출 후 usage를 ai_usage_log에 적재한다.
 * - 수동 검증용이므로 필요 시 호출.
 */
@RestController
@RequiredArgsConstructor
public class OpenAiSampleController {

    private final OpenAiClient openAiClient;

    @PostMapping("/api/openai/sample")
    public ResponseEntity<String> sample(@RequestParam Long workId,
                                         @RequestParam Long userId) {
        openAiClient.chatCompletion(
                AiFeatureType.CONTENT_GENERATE,
                workId,
                userId,
                java.util.List.of(OpenAiMessage.user("ping")),
                null
        );
        return ResponseEntity.ok("ok");
    }
}
