package com.ocp.ocp_finalproject.scheduler.job;

import com.ocp.ocp_finalproject.scheduler.service.SchedulerSyncService;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * startAt 시점에 워크플로우를 ACTIVE로 전환하고 실제 작업 스케줄을 등록하는 Quartz Job.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class WorkflowActivationJob implements Job {

    private final WorkflowRepository workflowRepository;
    private final SchedulerSyncService schedulerSyncService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long workflowId = context.getMergedJobDataMap().getLong("workflowId");
        Workflow workflow = workflowRepository.findByIdWithRecurrenceRule(workflowId)
                .orElseThrow(() -> new JobExecutionException("워크플로우를 찾을 수 없습니다. id=" + workflowId));

        if (workflow.getStatus() != WorkflowStatus.PENDING) {
            log.info("워크플로우 {} 활성화 Job을 건너뜁니다. 현재 상태={}", workflowId, workflow.getStatus());
            return;
        }

        workflow.changeStatus(WorkflowStatus.ACTIVE);
        log.info("워크플로우 {} 를 ACTIVE 상태로 전환했습니다. 실제 작업 스케줄을 등록합니다.", workflowId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    schedulerSyncService.registerWorkflowJobs(workflowId);
                } catch (Exception e) {
                    throw new RuntimeException("워크플로우 스케줄 등록에 실패했습니다. id=" + workflowId, e);
                }
            }
        });
    }
}
