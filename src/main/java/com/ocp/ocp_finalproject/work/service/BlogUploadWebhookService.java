package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.request.BlogUploadWebhookRequest;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.work.util.WebhookTimeParser;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogUploadWebhookService {

    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;

    @Transactional
    public void handleResult(BlogUploadWebhookRequest request) {
        Long workId = request.getWorkId();
        if (workId == null) {
            throw new CustomException(ErrorCode.WORK_NOT_FOUND, "워크 ID가 누락되었습니다.");
        }

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_NOT_FOUND, "워크를 찾을 수 없습니다. workId=" + workId));

        AiContent aiContent = aiContentRepository.findByWorkId(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CONTENT_NOT_FOUND, "콘텐츠를 찾을 수 없습니다. workId=" + workId));

        LocalDateTime completedAt = WebhookTimeParser.toUtcOrNow(request.getCompletedAt());

        boolean isSuccess = request.isSuccess();
        log.info("웹훅 결과 수신 workId={} success={} postingUrl={} completedAt={}", workId, isSuccess, request.getPostingUrl(), completedAt);
        work.updateUrlCompletion(request.getPostingUrl(), isSuccess, completedAt, request.getMessage());
        aiContent.updateBlogUploadResult(isSuccess, completedAt);

        boolean isTest = isTestRequest(request.getIsTest(), work);
        updateTestStatusIfNeeded(work, isTest, isSuccess);
    }

    private boolean isTestRequest(Boolean isTestFlag, Work work) {
        if (Boolean.TRUE.equals(isTestFlag)) {
            return true;
        }
        Workflow workflow = work.getWorkflow();
        return workflow != null && workflow.getStatus() == WorkflowStatus.PRE_REGISTERED;
    }

    private void updateTestStatusIfNeeded(Work work, boolean isTest, boolean isSuccess) {
        if (!isTest) {
            return;
        }

        Workflow workflow = work.getWorkflow();
        if (workflow == null) {
            return;
        }

        WorkflowTestStatus currentStatus = workflow.getTestStatus();

        if (!isSuccess) {
            if (currentStatus != WorkflowTestStatus.TEST_FAILED) {
                workflow.updateTestStatus(WorkflowTestStatus.TEST_FAILED);
                log.info("워크플로우 {} 테스트 실패 처리 - 블로그 업로드 단계", workflow.getId());
            }
            return;
        }

        if (currentStatus == WorkflowTestStatus.TEST_FAILED) {
            return;
        }

        workflow.updateTestStatus(WorkflowTestStatus.TEST_PASSED);
        log.info("워크플로우 {} 테스트 성공 처리 - 블로그 업로드 단계", workflow.getId());
    }
}
