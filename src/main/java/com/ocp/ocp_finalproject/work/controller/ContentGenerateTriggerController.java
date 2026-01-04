package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.integration.airflow.AirflowTriggerClient;
import com.ocp.ocp_finalproject.message.content.ContentGenerateProducer;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import com.ocp.ocp_finalproject.work.service.ContentGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class ContentGenerateTriggerController {

    private final ContentGenerateService contentGenerateService;
    private final ContentGenerateProducer contentGenerateProducer;
    private final AirflowTriggerClient airflowTriggerClient;

    @PostMapping("/{workflowId}/content-generate")
    public ResponseEntity<ApiResult<Void>> generate(@PathVariable Long workflowId) {
        ContentGenerateRequest request = contentGenerateService.createRequest(workflowId);
        contentGenerateService.applyWebhookSettings(request);
        contentGenerateService.markWorkRequested(request.getWorkId());
        contentGenerateProducer.send(request);

        // workId로 트리거 & dagRunId 받기
//        String dagRunId = airflowTriggerClient.triggerTrendPipeline(request.getWorkId());
//        log.info("Airflow DAG 트리거 완료 - workId: {}, dagRunId: {}", request.getWorkId(), dagRunId);

        return ResponseEntity.ok(ApiResult.success("콘텐츠 생성 요청을 전송했습니다."));
    }
}
