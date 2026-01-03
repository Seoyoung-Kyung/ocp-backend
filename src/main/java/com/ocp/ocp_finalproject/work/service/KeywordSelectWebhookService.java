package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.request.KeywordSelectWebhookRequest;
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
public class KeywordSelectWebhookService {

    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;

    @Transactional
    public void handleResult(KeywordSelectWebhookRequest request) {
        Long workId = request.getWorkId();
        if (workId == null) {
            throw new CustomException(ErrorCode.WORK_NOT_FOUND, "워크 ID가 누락되었습니다.");
        }

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_NOT_FOUND, "워크를 찾을 수 없습니다. workId=" + workId));

        AiContent ai = aiContentRepository.findByWorkId(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_CONTENT_NOT_FOUND,"콘텐츠를 찾을 수 없습니다. workId="+workId));


        LocalDateTime startedAt = WebhookTimeParser.toUtcOrNow(request.getStartedAt());
        LocalDateTime completedAt = WebhookTimeParser.toUtcOrNow(request.getCompletedAt());


        boolean isSuccess = request.isSuccess();
        log.info("웹훅 결과 수신 workId={} success={} keyword={} startedAt={} completedAt={}", workId, isSuccess, request.getKeyword(), startedAt, completedAt);

        work.updateKeywordCompletion(isSuccess, startedAt, completedAt, request.getMessage());
        ai.updateKeywordCompletion(isSuccess, request.getKeyword(), startedAt, completedAt);

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
        if (!isTest || isSuccess) {
            return;
        }
        Workflow workflow = work.getWorkflow();
        if (workflow == null) {
            return;
        }
        if (workflow.getTestStatus() == WorkflowTestStatus.TEST_FAILED) {
            return;
        }
        workflow.updateTestStatus(WorkflowTestStatus.TEST_FAILED);
        log.info("워크플로우 {} 테스트 실패 처리 - 키워드 선택 단계", workflow.getId());
    }
}
