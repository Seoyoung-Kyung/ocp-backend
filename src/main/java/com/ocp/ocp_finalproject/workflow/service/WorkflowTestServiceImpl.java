package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import com.ocp.ocp_finalproject.blog.domain.UserBlog;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.integration.airflow.AirflowTriggerClient;
import com.ocp.ocp_finalproject.message.content.ContentGenerateProducer;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import com.ocp.ocp_finalproject.trend.repository.TrendCategoryRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.work.service.ContentGenerateService;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import com.ocp.ocp_finalproject.workflow.dto.SetTrendCategoryNameDto;
import com.ocp.ocp_finalproject.workflow.dto.request.WorkflowRequest;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowTestDetailResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowTestResponse;
import com.ocp.ocp_finalproject.workflow.enums.SiteUrlInfo;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.INVALID_INPUT_VALUE;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.NOT_WORKFLOW_OWNER;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.TREND_NOT_FOUND;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.UNAUTHORIZED;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.WORKFLOW_NOT_FOUND;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.WORKFLOW_TEST_NOT_PASSED;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTestServiceImpl implements WorkflowTestService {

    private final WorkflowService workflowService;
    private final ContentGenerateService contentGenerateService;
    private final ContentGenerateProducer contentGenerateProducer;
    private final AirflowTriggerClient airflowTriggerClient;
    private final WorkflowRepository workflowRepository;
    private final WorkRepository workRepository;
    private final TrendCategoryRepository trendCategoryRepository;

    @Override
    @Transactional
    public WorkflowTestResponse executeWorkflowTest(
            UserPrincipal principal,
            WorkflowRequest workflowRequest,
            Long replaceWorkflowId
    ) throws SchedulerException {
        // 1. 사용자 ID 검증
        Long userId = validateAndGetUserId(principal);

        if (replaceWorkflowId != null) {
            Workflow replaceWorkflow = workflowRepository.findWorkflow(userId, replaceWorkflowId)
                    .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));
            if (replaceWorkflow.getTestStatus() != WorkflowTestStatus.TEST_PASSED) {
                throw new CustomException(WORKFLOW_TEST_NOT_PASSED);
            }
        }

        // 2. 워크플로우 생성
        WorkflowResponse workflow = workflowService.createWorkflowDraft(userId, workflowRequest);

        Long workflowId = workflow.getWorkflowId();

        workflowService.markWorkflowAsTest(workflowId);

        // 3. 콘텐츠 생성 요청 전송
        generateContent(workflowId);

        // 4. Airflow 트렌드 파이프라인 트리거
        triggerAirflow(workflowId);

        log.info("워크플로우 {} 테스트 요청 완료: 콘텐츠 생성 메시지 전송 후 Airflow에 전달", workflowId);

        // 6. 응답 생성
        return WorkflowTestResponse.builder()
                .workflowId(workflowId)
                .contentGenerateRequested(true)
                .blogUploadCount(0)
                .testStatus(WorkflowTestStatus.TESTING)
                .message("워크플로우 생성 및 테스트 실행 완료")
                .build();
    }


    private Long validateAndGetUserId(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        return principal.getUser().getId();
    }

    // 3. 콘텐츠 생성 요청 전송
    private void generateContent(Long workflowId) {
        ContentGenerateRequest contentRequest = contentGenerateService.createRequest(workflowId);
        contentGenerateService.applyWebhookSettings(contentRequest);
        contentGenerateService.markWorkRequested(contentRequest.getWorkId());
        contentGenerateProducer.send(contentRequest);
        log.info("워크플로우 {} 콘텐츠 생성 메시지 전송 완료", workflowId);
    }

    // 4. Airflow 트렌드 파이프라인 트리거
    private void triggerAirflow(Long workflowId) {
        airflowTriggerClient.triggerTrendPipeline(workflowId);
        log.info("워크플로우 {} Airflow 파이프라인 트리거 완료", workflowId);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowTestDetailResponse getTestWorkflow(Long workflowId, UserPrincipal principal) {
        Long userId = validateAndGetUserId(principal);

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        if (workflow.getUser() == null || workflow.getUser().getId() == null || !workflow.getUser().getId().equals(userId)) {
            throw new CustomException(NOT_WORKFLOW_OWNER);
        }

        if (workflow.getStatus() != WorkflowStatus.PRE_REGISTERED) {
            throw new CustomException(INVALID_INPUT_VALUE, "테스트 워크플로우가 아닙니다.");
        }

        User user = workflow.getUser();
        UserBlog userBlog = workflow.getUserBlog();
        if (userBlog == null) {
            throw new CustomException(INVALID_INPUT_VALUE, "워크플로우에 블로그 정보가 없습니다.");
        }
        BlogType blogType = userBlog.getBlogType();

        TrendCategory trendCategory = workflow.getTrendCategory();
        if (trendCategory == null) {
            throw new CustomException(INVALID_INPUT_VALUE, "워크플로우에 트렌드 카테고리 정보가 없습니다.");
        }

        TrendCategory category = trendCategoryRepository.findCategoryWithParent(trendCategory.getId())
                .orElseThrow(() -> new CustomException(TREND_NOT_FOUND));

        WorkflowTestDetailResponse.TestWorkInfo latestWork = workRepository.findTopByWorkflowIdOrderByCreatedAtDesc(workflowId)
                .map(work -> WorkflowTestDetailResponse.TestWorkInfo.builder()
                        .workId(work.getId())
                        .status(work.getStatus())
                        .completedAt(work.getCompletedAt())
                        .failureReason(work.getFailureReason())
                        .build())
                .orElse(null);

        return WorkflowTestDetailResponse.builder()
                .workflowId(workflow.getId())
                .userId(user.getId())
                .userName(user.getName())
                .siteName(SiteUrlInfo.getSiteNameFromUrl(workflow.getSiteUrl()))
                .siteUrl(workflow.getSiteUrl())
                .blogType(blogType.getBlogTypeName())
                .blogUrl(userBlog.getBlogUrl())
                .blogAccountId(userBlog.getAccountId())
                .setTrendCategory(SetTrendCategoryNameDto.from(category))
                .recurrenceRule(RecurrenceRuleDto.from(workflow.getRecurrenceRule()))
                .status(workflow.getStatus())
                .testStatus(workflow.getTestStatus())
                .latestWork(latestWork)
                .build();
    }
}
