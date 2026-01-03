package com.ocp.ocp_finalproject.scheduler.init;

import com.ocp.ocp_finalproject.scheduler.service.SchedulerSyncService;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final WorkflowRepository workflowRepository;
    private final SchedulerSyncService schedulerSyncService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        // 1. 스케줄러 시작
        schedulerSyncService.startSchedulerIfNeeded();

        // 2. 활성 워크플로우 등록
        List<Workflow> workflows = workflowRepository.findAllActive();
        workflows.forEach(workflow -> {
            try {
                schedulerSyncService.registerWorkflowJobs(workflow);
            } catch (SchedulerException e) {
                // 예외 처리: 로그 출력 등
                log.error("워크플로우 스케줄 등록 실패: {}", workflow.getId(), e);
            }
        });

        // 3. 대기 워크플로우 활성화 Job 등록
        List<Workflow> pendingWorkflows = workflowRepository.findAllPending();
        pendingWorkflows.forEach(workflow -> {
            try {
                schedulerSyncService.registerActivationJob(workflow);
            } catch (SchedulerException e) {
                log.error("워크플로우 활성화 스케줄 등록 실패: {}", workflow.getId(), e);
            }
        });
    }
}
