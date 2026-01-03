package com.ocp.ocp_finalproject.scheduler.job;

import com.ocp.ocp_finalproject.scheduler.service.SchedulerSyncService;
import com.ocp.ocp_finalproject.workflow.service.WorkflowExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class WorkflowExpirationJob implements Job {

    private final WorkflowExpirationService workflowExpirationService;
    private final SchedulerSyncService schedulerSyncService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long workflowId = context.getMergedJobDataMap().getLong("workflowId");
        log.info("워크플로우 {} 만료 잡 실행", workflowId);

        boolean completed = workflowExpirationService.completeIfExpired(workflowId);
        if (completed) {
            log.info("워크플로우 {} 만료 처리 완료, 관련 스케줄 삭제", workflowId);
            schedulerSyncService.removeWorkflowJobs(workflowId);
        } else {
            log.info("워크플로우 {} 만료 처리 조건 불충족, 스케줄 삭제 없이 종료", workflowId);
        }
    }
}
