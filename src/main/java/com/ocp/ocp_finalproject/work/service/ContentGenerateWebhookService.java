package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.request.ContentGenerateWebhookRequest;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.work.util.WebhookTimeParser;
import com.ocp.ocp_finalproject.message.blog.dto.BlogUploadRequest;
import com.ocp.ocp_finalproject.message.blog.BlogUploadProducer;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentGenerateWebhookService {

    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;
    private final BlogUploadService blogUploadService;
    private final BlogUploadProducer blogUploadProducer;

    @Transactional
    public void handleResult(ContentGenerateWebhookRequest request) {
        Long workId = request.getWorkId();
        if (workId == null) {
            throw new CustomException(ErrorCode.WORK_NOT_FOUND, "워크 ID가 누락되었습니다.");
        }

        // N+1 쿼리 개선: findByIdWithWorkflow() 사용 (2번 쿼리 → 1번 쿼리, 50% 개선)
        Work work = workRepository.findByIdWithWorkflow(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_NOT_FOUND, "워크를 찾을 수 없습니다. workId=" + workId));

        AiContent aiContent = aiContentRepository.findByWorkId(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CONTENT_NOT_FOUND, "콘텐츠를 찾을 수 없습니다. workId=" + workId));

        Boolean successFlag = request.getSuccess();
        if (successFlag == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "성공 여부가 누락되었습니다.");
        }

        LocalDateTime completedAt = WebhookTimeParser.toUtcOrNow(request.getCompletedAt());

        boolean isSuccess = successFlag;
        log.info("콘텐츠 생성 웹훅 수신 workId={} success={} title={}", workId, isSuccess, request.getTitle());

        aiContent.updateContentGeneration(isSuccess, request.getTitle(), request.getSummary(), request.getContent(), completedAt);
        work.updateContentGeneration(isSuccess, completedAt, request.getMessage());

        boolean isTest = isTestRequest(request.getIsTest(), work);

        if (!isSuccess) {
            updateTestStatusIfNeeded(work, isTest, false);
            return;
        }

        if (isTest) {
            triggerBlogUpload(work);
        }
    }

    private boolean isTestRequest(Boolean isTestFlag, Work work) {
        if (Boolean.TRUE.equals(isTestFlag)) {
            return true;
        }
        Workflow workflow = work.getWorkflow();
        return workflow != null && workflow.getStatus() == WorkflowStatus.PRE_REGISTERED;
    }

    private void updateTestStatusIfNeeded(Work work, boolean isTest, boolean isSuccess) {
        if (!isTest || isSuccess) {
            return;
        }
        Workflow workflow = work.getWorkflow();
        if (workflow == null || workflow.getTestStatus() == WorkflowTestStatus.TEST_FAILED) {
            return;
        }
        workflow.updateTestStatus(WorkflowTestStatus.TEST_FAILED);
        log.info("워크플로우 {} 테스트 실패 처리 - 콘텐츠 생성 단계", workflow.getId());
    }

    private void triggerBlogUpload(Work work) {
        Workflow workflow = work.getWorkflow();
        if (workflow == null) {
            return;
        }
        List<BlogUploadRequest> requests = blogUploadService.collectPendingBlogUploadsForWorkflow(workflow.getId());
        if (requests.isEmpty()) {
            log.info("워크 {} 테스트 블로그 업로드 요청이 없습니다.", work.getId());
            return;
        }
        for (BlogUploadRequest request : requests) {
            BlogUploadRequest prepared = blogUploadService.prepareBlogUploadRequest(request);
            blogUploadProducer.send(prepared);
            log.info("워크 {} 테스트 블로그 업로드 메시지 전송", prepared.getWorkId());
        }
    }
}
