package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import com.ocp.ocp_finalproject.blog.domain.UserBlog;
import com.ocp.ocp_finalproject.blog.repository.BlogTypeRepository;
import com.ocp.ocp_finalproject.blog.repository.UserBlogRepository;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.scheduler.service.SchedulerSyncService;
import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import com.ocp.ocp_finalproject.trend.repository.TrendCategoryRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.workflow.domain.*;
import com.ocp.ocp_finalproject.workflow.dto.*;
import com.ocp.ocp_finalproject.workflow.dto.request.*;
import com.ocp.ocp_finalproject.workflow.dto.response.*;
import com.ocp.ocp_finalproject.workflow.enums.SiteUrlInfo;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import com.ocp.ocp_finalproject.workflow.dto.response.GetWorkflowResponse;
import com.ocp.ocp_finalproject.workflow.util.AesCryptoUtil;
import com.ocp.ocp_finalproject.workflow.validator.RecurrenceRuleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.*;
import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final TrendCategoryRepository trendCategoryRepository;
    private final BlogTypeRepository blogTypeRepository;
    private final UserBlogRepository userBlogRepository;
    private final RecurrenceRuleValidator validator;
    private final SchedulerSyncService schedulerSyncService;
    private final AesCryptoUtil aesCryptoUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<WorkflowListResponse> getWorkflows(Long userId, int page) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        PageRequest pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<WorkflowListResponse> workflows = workflowRepository.findWorkflows(user.getId(), pageable);

        return workflows.map(wf -> WorkflowListResponse.builder()
                .workflowId(wf.getWorkflowId())
                .userId(wf.getUserId())
                .siteUrl(wf.getSiteUrl())
                .siteName(SiteUrlInfo.getSiteNameFromUrl(wf.getSiteUrl()))
                .blogType(wf.getBlogType())
                .blogUrl(wf.getBlogUrl())
                .trendCategoryName(wf.getTrendCategoryName())
                .blogAccountId(wf.getBlogAccountId())
                .readableRule(wf.getReadableRule())
                .status(wf.getStatus())
                .testStatus(wf.getTestStatus())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowEditResponse getWorkflowForEdit(Long workflowId, Long userId) {

        Workflow workflow = workflowRepository.findWorkflow(userId, workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        User user = workflow.getUser();

        UserBlog userBlog = workflow.getUserBlog();

        BlogType blogType = userBlog.getBlogType();

        TrendCategory category = trendCategoryRepository.findCategoryWithParent(workflow.getTrendCategory().getId())
                .orElseThrow(() -> new CustomException(TREND_NOT_FOUND));

        RecurrenceRule rule = workflow.getRecurrenceRule();

        return WorkflowEditResponse.builder()
                .workflowId(workflow.getId())
                .userId(user.getId())
                .siteUrl(workflow.getSiteUrl())
                .blogTypeId(blogType.getId())
                .blogUrl(userBlog.getBlogUrl())
                .setTrendCategory(SetTrendCategoryIdDto.from(category))
                .blogAccountId(userBlog.getAccountId())
                .recurrenceRule(RecurrenceRuleDto.from(rule))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GetWorkflowResponse getWorkflow(Long workflowId, Long userId) {

        Workflow workflow = workflowRepository.findWorkflow(userId, workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        User user = workflow.getUser();

        UserBlog userBlog = workflow.getUserBlog();

        BlogType blogType = userBlog.getBlogType();

        TrendCategory category = trendCategoryRepository.findCategoryWithParent(workflow.getTrendCategory().getId())
                .orElseThrow(() -> new CustomException(TREND_NOT_FOUND));

        RecurrenceRule rule = workflow.getRecurrenceRule();

        return GetWorkflowResponse.builder()
                .workflowId(workflow.getId())
                .userId(user.getId())
                .userName(user.getName())
                .siteName(SiteUrlInfo.getSiteNameFromUrl(workflow.getSiteUrl()))
                .siteUrl(workflow.getSiteUrl())
                .blogType(blogType.getBlogTypeName())
                .blogUrl(userBlog.getBlogUrl())
                .blogAccountId(userBlog.getAccountId())
                .setTrendCategory(SetTrendCategoryNameDto.from(category))
                .recurrenceRule(RecurrenceRuleDto.from(rule))
                .status(workflow.getStatus())
                .testStatus(workflow.getTestStatus())
                .build();
    }

    @Override
    @Transactional
    public WorkflowResponse createWorkflowDraft(Long userId, WorkflowRequest workflowRequest) throws SchedulerException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        BlogType blogType = blogTypeRepository.findById(workflowRequest.getBlogTypeId())
                .orElseThrow(() -> new CustomException(BLOG_TYPE_NOT_FOUND));

        TrendCategory category = trendCategoryRepository.findCategoryWithParent(workflowRequest.getCategoryId())
                .orElseThrow(() -> new CustomException(TREND_NOT_FOUND));


        UserBlog userBlog = UserBlog.create(blogType,
                workflowRequest.getBlogAccountId(),
                aesCryptoUtil.encrypt(workflowRequest.getBlogAccountPwd()),
                workflowRequest.getBlogUrl());

        RecurrenceRuleDto ruleDto = workflowRequest.getRecurrenceRule();

        validator.validate(ruleDto);

        RecurrenceRule rule = RecurrenceRule.create(ruleDto.getRepeatType(),
                ruleDto.getRepeatInterval(),
                ruleDto.getDaysOfWeek(),
                ruleDto.getDaysOfMonth(),
                ruleDto.getTimesOfDay(),
                ruleDto.getStartAt(),
                ruleDto.getEndAt());

        Workflow workflow = Workflow.create(user, userBlog, category, rule, workflowRequest.getSiteUrl());

        workflowRepository.save(workflow);

        return buildResponse(workflow, category);
    }

    @Override
    @Transactional
    public WorkflowResponse registerWorkflow(Long userId, Long workflowId, Long replaceWorkflowId) throws SchedulerException {
        Workflow draftWorkflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        if (!draftWorkflow.getUser().getId().equals(userId)) {
            throw new CustomException(NOT_WORKFLOW_OWNER);
        }

        if (draftWorkflow.getTestStatus() != WorkflowTestStatus.TEST_PASSED) {
            throw new CustomException(ErrorCode.WORKFLOW_TEST_NOT_PASSED);
        }

        Workflow targetWorkflow;
        if (replaceWorkflowId != null) {
            targetWorkflow = workflowRepository.findWorkflow(userId, replaceWorkflowId)
                    .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));
            UserBlog blogCopy = copyUserBlog(draftWorkflow.getUserBlog());
            RecurrenceRule ruleCopy = copyRecurrenceRule(draftWorkflow.getRecurrenceRule());
            targetWorkflow.update(blogCopy, draftWorkflow.getTrendCategory(), ruleCopy, draftWorkflow.getSiteUrl());
        } else {
            targetWorkflow = Workflow.create(
                    draftWorkflow.getUser(),
                    copyUserBlog(draftWorkflow.getUserBlog()),
                    draftWorkflow.getTrendCategory(),
                    copyRecurrenceRule(draftWorkflow.getRecurrenceRule()),
                    draftWorkflow.getSiteUrl()
            );
            workflowRepository.save(targetWorkflow);
        }
        targetWorkflow.updateTestStatus(WorkflowTestStatus.TEST_PASSED);
        targetWorkflow.changeStatus(WorkflowStatus.PENDING);

        workflowRepository.delete(draftWorkflow);

        scheduleJobsAfterCommit(targetWorkflow.getId(), targetWorkflow.getStatus());
        return buildResponse(targetWorkflow, targetWorkflow.getTrendCategory());
    }

    private UserBlog copyUserBlog(UserBlog original) {
        if (original == null) {
            return null;
        }
        return UserBlog.create(
                original.getBlogType(),
                original.getAccountId(),
                original.getAccountPassword(),
                original.getBlogUrl()
        );
    }

    private RecurrenceRule copyRecurrenceRule(RecurrenceRule original) {
        if (original == null) {
            return null;
        }
        List<Integer> daysOfWeek = original.getDaysOfWeek() != null
                ? new ArrayList<>(original.getDaysOfWeek())
                : null;
        List<Integer> daysOfMonth = original.getDaysOfMonth() != null
                ? new ArrayList<>(original.getDaysOfMonth())
                : null;
        List<String> timesOfDay = original.getTimesOfDay() != null
                ? new ArrayList<>(original.getTimesOfDay())
                : null;

        return RecurrenceRule.create(
                original.getRepeatType(),
                original.getRepeatInterval(),
                daysOfWeek,
                daysOfMonth,
                timesOfDay,
                original.getStartAt(),
                original.getEndAt()
        );
    }

    @Override
    @Transactional
    public WorkflowStatusResponse updateStatus(Long userId, Long workflowId, WorkflowStatus newStatus) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Workflow workflow = workflowRepository.findWorkflow(userId, workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        workflow.changeStatus(newStatus);
        WorkflowStatus updatedStatus = workflow.getStatus();

        scheduleJobsAfterCommit(workflowId, updatedStatus);

        return WorkflowStatusResponse.builder()
                .workflowId(workflow.getId())
                .status(newStatus)
                .changedAt(workflow.getUpdatedAt())
                .build();
    }


    @Override
    @Transactional
    public WorkflowStatusResponse deleteWorkflow(Long userId, Long workflowId) {
        Workflow workflow = workflowRepository.findWorkflow(userId, workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        if (workflow.getStatus() == WorkflowStatus.DELETED) {
            throw new CustomException(ALREADY_DELETED);
        }

        workflow.delete();

        scheduleJobsAfterCommit(workflowId, workflow.getStatus());

        return WorkflowStatusResponse.builder()
                .workflowId(workflow.getId())
                .status(workflow.getStatus())
                .changedAt(workflow.getDeletedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendCategoryResponse> getTrendCategories() {

        List<TrendCategory> roots = trendCategoryRepository.findByParentCategoryIsNull();

        return roots.stream()
                .map(TrendCategoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogTypeResponse> getBlogTypes() {

        List<BlogType> blogTypes = blogTypeRepository.findAll();

        return blogTypes.stream()
                .map(BlogTypeResponse::from)
                .toList();
    }

    private void scheduleJobsAfterCommit(Long workflowId, WorkflowStatus status) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            if (status == WorkflowStatus.PENDING) {
                                schedulerSyncService.registerActivationJob(workflowId);
                            } else if (status == WorkflowStatus.ACTIVE) {
                                schedulerSyncService.updateWorkflowJobs(workflowId);
                            } else {
                                schedulerSyncService.removeWorkflowJobs(workflowId);
                                schedulerSyncService.removeActivationJob(workflowId);
                            }
                        } catch (SchedulerException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    private WorkflowResponse buildResponse(
            Workflow workflow,
            TrendCategory category
    ) {
        User user = workflow.getUser();
        UserBlog userBlog = workflow.getUserBlog();
        BlogType blogType = userBlog.getBlogType();
        RecurrenceRule rule = workflow.getRecurrenceRule();

        return WorkflowResponse.builder()
                .workflowId(workflow.getId())
                .userId(user.getId())
                .siteUrl(workflow.getSiteUrl())
                .blogType(blogType.getBlogTypeName())
                .blogUrl(userBlog.getBlogUrl())
                .setTrendCategory(SetTrendCategoryIdDto.from(category))
                .blogAccountId(userBlog.getAccountId())
                .readableRule(rule.getReadableRule())
                .build();
    }

    @Override
    @Transactional
    public void markWorkflowAsTest(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));
        workflow.markAsTest();
        log.info("워크플로우 {} 테스트 모드로 설정 완료", workflowId);
    }
}
