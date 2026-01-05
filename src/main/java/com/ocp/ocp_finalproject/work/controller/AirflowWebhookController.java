package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.monitoring.service.AirflowLogCollectorService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhook/airflow")
public class AirflowWebhookController {

    private final AirflowLogCollectorService logCollectorService;

    @PostMapping("/logs")
    public ResponseEntity<ApiResult<Void>> collectLogs(
            @RequestBody AirflowLogRequest request
    ) {
//        log.info("Airflow 로그 수집 요청 - workId: {}, dagId: {}, runId: {}",
//                request.getWorkId(), request.getDagId(), request.getRunId());
//
//        logCollectorService.collectAndSaveLogs(
//                request.getWorkId(),
//                request.getDagId(),
//                request.getRunId()
//        );

        return ResponseEntity.ok(ApiResult.success("로그 수집 완료", null));
    }

    // Request DTO
    @Data
    public static class AirflowLogRequest {
        private Long workId;
        private String dagId;    // "trend_pipeline"
        private String runId;    // "manual__2025-12-09T16:49:31.748347+00:00"
    }
}
