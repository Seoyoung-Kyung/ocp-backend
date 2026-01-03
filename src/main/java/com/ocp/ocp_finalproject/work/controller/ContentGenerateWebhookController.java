package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.audit.annotation.Audit;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.work.config.ContentGenerateProperties;
import com.ocp.ocp_finalproject.work.dto.request.ContentGenerateWebhookRequest;
import com.ocp.ocp_finalproject.work.service.ContentGenerateWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/work/content/webhook")
@RequiredArgsConstructor
public class ContentGenerateWebhookController {

    private static final String WEBHOOK_HEADER = "X-WEBHOOK-SECRET";

    private final ContentGenerateProperties contentGenerateProperties;
    private final ContentGenerateWebhookService webhookService;

    @Audit(
            action = AuditAction.WEBHOOK_CONTENT_GENERATE_RESULT,
            actorType = ActorType.SYSTEM,
            targetType = "WORK",
            targetIdSpel = "#request.workId"
    )
    @PostMapping
    public ApiResult<Void> handleWebhook(
            @RequestHeader(value = WEBHOOK_HEADER, required = false) String secretHeader,
            @RequestBody ContentGenerateWebhookRequest request
    ) {
        validateSecret(secretHeader);
        webhookService.handleResult(request);
        return ApiResult.success("콘텐츠 생성 결과를 처리했습니다.");
    }

    private void validateSecret(String secretHeader) {
        String expectedSecret = contentGenerateProperties.getWebhookSecret();
        if (expectedSecret == null || expectedSecret.isBlank()) {
            log.warn("콘텐츠 생성 웹훅 시크릿이 설정되지 않았습니다.");
            throw new CustomException(ErrorCode.WORK_WEBHOOK_TOKEN_INVALID, "웹훅 시크릿이 설정되지 않았습니다.");
        }

        if (secretHeader == null || !secretHeader.equals(expectedSecret)) {
            throw new CustomException(ErrorCode.WORK_WEBHOOK_TOKEN_INVALID);
        }
    }
}
