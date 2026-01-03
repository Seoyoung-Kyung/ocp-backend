package com.ocp.ocp_finalproject.scheduler.service;

import com.ocp.ocp_finalproject.scheduler.job.BlogUploadJob;
import com.ocp.ocp_finalproject.scheduler.job.ContentGenerationJob;
import com.ocp.ocp_finalproject.scheduler.job.WorkflowActivationJob;
import com.ocp.ocp_finalproject.scheduler.job.WorkflowExpirationJob;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import com.ocp.ocp_finalproject.workflow.util.RecurrenceRuleCronConverter;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerSyncService {

    private static final Duration CONTENT_JOB_OFFSET = Duration.ofHours(-1);
    private static final String CONTENT_JOB_PREFIX = "content-generate-";
    private static final String CONTENT_TRIGGER_PREFIX = "content-generate-trigger-";
    private static final String UPLOAD_JOB_PREFIX = "blog-upload-";
    private static final String UPLOAD_TRIGGER_PREFIX = "blog-upload-trigger-";
    private static final String EXPIRATION_JOB_PREFIX = "workflow-expiration-";
    private static final String EXPIRATION_TRIGGER_PREFIX = "workflow-expiration-trigger-";
    private static final String ACTIVATION_JOB_PREFIX = "workflow-activation-";
    private static final String ACTIVATION_TRIGGER_PREFIX = "workflow-activation-trigger-";

    private  final WorkflowRepository workflowRepository;
    private final Scheduler scheduler;

    public void startSchedulerIfNeeded() throws SchedulerException {
        if (!scheduler.isStarted()) {
            scheduler.start();  // 스케줄러 시작
        }
    }

    public void registerWorkflowJobs(Long workflowId) throws SchedulerException {
        Workflow workflow = workflowRepository.findByIdWithRecurrenceRule(workflowId)
                .orElseThrow();

        registerWorkflowJobs(workflow);
    }

    public void registerWorkflowJobs(Workflow workflow) throws SchedulerException {
        if (workflow.getStatus() != WorkflowStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 상태의 워크플로우만 스케줄링할 수 있습니다. id=" + workflow.getId());
        }

        removeActivationJob(workflow.getId());

        // 1. 콘텐츠 생성 Job/Trigger 생성 (블로그 업로드보다 1시간 빠르게 실행)
        JobDetail contentJob = JobBuilder.newJob(ContentGenerationJob.class)
                .withIdentity(CONTENT_JOB_PREFIX + workflow.getId())
                .usingJobData("workflowId", workflow.getId())
                .storeDurably()
                .build();

        List<String> contentCronExpressions = RecurrenceRuleCronConverter
                .toCronExpressionsWithOffset(workflow.getRecurrenceRule(), CONTENT_JOB_OFFSET);
        if (contentCronExpressions.isEmpty()) {
            throw new IllegalStateException("콘텐츠 생성 Cron 표현식을 하나 이상 생성해야 합니다.");
        }
        List<Trigger> contentTriggers = buildTriggers(
                contentJob,
                contentCronExpressions,
                CONTENT_TRIGGER_PREFIX + workflow.getId()
        );
        if (scheduler.checkExists(contentJob.getKey())) {
            scheduler.deleteJob(contentJob.getKey());
        }
        scheduler.scheduleJob(contentJob, new HashSet<>(contentTriggers), true);

        // 2. 블로그 업로드 Job
        JobDetail uploadJob = JobBuilder.newJob(BlogUploadJob.class)
                .withIdentity(UPLOAD_JOB_PREFIX + workflow.getId())
                .usingJobData("workflowId", workflow.getId())
                .storeDurably()
                .build();

        List<String> cronExpressions = RecurrenceRuleCronConverter.toCronExpressions(workflow.getRecurrenceRule());
        if (cronExpressions.isEmpty()) {
            throw new IllegalStateException("Blog upload Cron 표현식을 하나 이상 생성해야 합니다.");
        }
        List<Trigger> uploadTriggers = buildTriggers(
                uploadJob,
                cronExpressions,
                UPLOAD_TRIGGER_PREFIX + workflow.getId()
        );

        if (scheduler.checkExists(uploadJob.getKey())) {
            scheduler.deleteJob(uploadJob.getKey());
        }
        Set<Trigger> triggerSet = new HashSet<>(uploadTriggers);
        scheduler.scheduleJob(uploadJob, triggerSet, true);

        // 3. 만료 Job
        registerExpirationJob(workflow);
    }

    public void updateWorkflowJobs(Long workflowId) throws SchedulerException {
        removeWorkflowJobs(workflowId);
        registerWorkflowJobs(workflowId);
    }

    public void removeWorkflowJobs(Long workflowId) {
        try {
            scheduler.deleteJob(new JobKey(CONTENT_JOB_PREFIX + workflowId));
            scheduler.deleteJob(new JobKey(UPLOAD_JOB_PREFIX + workflowId));
            scheduler.deleteJob(new JobKey(EXPIRATION_JOB_PREFIX + workflowId));
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    public void registerActivationJob(Long workflowId) throws SchedulerException {
        Workflow workflow = workflowRepository.findByIdWithRecurrenceRule(workflowId)
                .orElseThrow();
        registerActivationJob(workflow);
    }

    public void registerActivationJob(Workflow workflow) throws SchedulerException {
        if (workflow.getRecurrenceRule() == null || workflow.getRecurrenceRule().getStartAt() == null) {
            removeActivationJob(workflow.getId());
            throw new IllegalStateException("워크플로우에 startAt 정보가 없습니다. id=" + workflow.getId());
        }
        if (workflow.getStatus() != WorkflowStatus.PENDING) {
            removeActivationJob(workflow.getId());
            return;
        }

        removeWorkflowJobs(workflow.getId());

        JobKey jobKey = new JobKey(ACTIVATION_JOB_PREFIX + workflow.getId());
        JobDetail activationJob = JobBuilder.newJob(WorkflowActivationJob.class)
                .withIdentity(jobKey)
                .usingJobData("workflowId", workflow.getId())
                .build();

        Trigger activationTrigger = TriggerBuilder.newTrigger()
                .withIdentity(ACTIVATION_TRIGGER_PREFIX + workflow.getId())
                .forJob(activationJob)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .startAt(Date.from(workflow.getRecurrenceRule().getStartAt()
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        scheduler.scheduleJob(activationJob, activationTrigger);
    }

    public void removeActivationJob(Long workflowId) {
        try {
            scheduler.deleteJob(new JobKey(ACTIVATION_JOB_PREFIX + workflowId));
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Trigger> buildTriggers(JobDetail job, List<String> cronExpressions, String baseTriggerKey) {
        List<Trigger> triggers = new ArrayList<>();
        for (int i = 0; i < cronExpressions.size(); i++) {
            String triggerId = cronExpressions.size() == 1 ? baseTriggerKey : baseTriggerKey + "-" + (i + 1);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerId)
                    .forJob(job)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressions.get(i)))
                    .build();
            triggers.add(trigger);
        }
        return triggers;
    }

    private void registerExpirationJob(Workflow workflow) throws SchedulerException {
        if (workflow.getRecurrenceRule() == null || workflow.getRecurrenceRule().getEndAt() == null) {
            removeExpirationJob(workflow.getId());
            return;
        }

        JobDetail expirationJob = JobBuilder.newJob(WorkflowExpirationJob.class)
                .withIdentity(EXPIRATION_JOB_PREFIX + workflow.getId())
                .usingJobData("workflowId", workflow.getId())
                .storeDurably()
                .build();

        Trigger expirationTrigger = TriggerBuilder.newTrigger()
                .withIdentity(EXPIRATION_TRIGGER_PREFIX + workflow.getId())
                .forJob(expirationJob)
                .startAt(Date.from(workflow.getRecurrenceRule().getEndAt()
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        if (scheduler.checkExists(expirationJob.getKey())) {
            scheduler.deleteJob(expirationJob.getKey());
        }
        scheduler.scheduleJob(expirationJob, expirationTrigger);
    }

    private void removeExpirationJob(Long workflowId) throws SchedulerException {
        JobKey expirationJobKey = new JobKey(EXPIRATION_JOB_PREFIX + workflowId);
        if (scheduler.checkExists(expirationJobKey)) {
            scheduler.deleteJob(expirationJobKey);
        }
    }
}
