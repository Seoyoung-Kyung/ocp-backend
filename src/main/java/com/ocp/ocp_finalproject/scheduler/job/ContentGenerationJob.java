package com.ocp.ocp_finalproject.scheduler.job;

import com.ocp.ocp_finalproject.integration.airflow.AirflowTriggerClient;
import com.ocp.ocp_finalproject.message.content.ContentGenerateProducer;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import com.ocp.ocp_finalproject.work.service.ContentGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ContentGenerationJob implements Job {

    private final ContentGenerateService contentGenerateService;
    private final ContentGenerateProducer contentGenerateProducer;
    private final AirflowTriggerClient airflowTriggerClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long workflowId = context.getMergedJobDataMap().getLong("workflowId");

        log.info("Workflow {} 콘텐츠 생성 스케줄 실행", workflowId);

        ContentGenerateRequest request = contentGenerateService.createRequest(workflowId);
        ContentGenerateRequest prepared = contentGenerateService.applyWebhookSettings(request);
        contentGenerateProducer.send(prepared);
        log.info("워크 {} 콘텐트 생성 메시지 전송 (Python worker 사용)", prepared.getWorkId());

        // Airflow 대신 Python worker 사용으로 변경됨 - Airflow 트리거 비활성화
        // String dagRunId = airflowTriggerClient.triggerTrendPipeline(prepared.getWorkId());
        // log.info("스케줄 실행 - workId: {}, dagRunId: {}", prepared.getWorkId(), dagRunId);
    }
}
