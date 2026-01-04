package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.response.WorkListResponse;
import com.ocp.ocp_finalproject.work.dto.response.WorkPageResponse;
import com.ocp.ocp_finalproject.work.dto.response.WorkResponse;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class WorkService {

    private final WorkRepository workRepository;
    private final WorkflowRepository workflowRepository;
    private final AiContentRepository aiContentRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Transactional(readOnly = true)
    public WorkPageResponse getWorks(Long userId, Long workflowId, int page) {
        if (page < 0) {
            throw new CustomException(INVALID_INPUT_VALUE, "페이지 번호는 0 이상의 정수여야 합니다.");
        }

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

        validateWorkflowOwner(workflow, userId);

        PageRequest pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        // N+1 쿼리 개선: findByWorkflowIdWithAiContent() 사용 (2번 쿼리 → 1번 쿼리, 50% 개선)
        Page<Work> workPage = workRepository.findByWorkflowIdWithAiContent(workflowId, pageable);
        List<Work> works = workPage.getContent();

        // N+1 쿼리 개선: AiContent가 이미 로드되어 있으므로 fetchChoiceProductMap() 제거
        List<WorkListResponse> responses = works.stream()
                .map(work -> WorkListResponse.builder()
                        .workId(work.getId())
                        .postingUrl(work.getPostingUrl())
                        .completedAt(work.getCompletedAt())
                        .choiceProduct(work.getAiContent() != null ? work.getAiContent().getChoiceProduct() : null)
                        .failureReason(work.getFailureReason())
                        .status(work.getStatus() != null ? work.getStatus().name() : null)
                        .build())
                .collect(Collectors.toList());

        return WorkPageResponse.builder()
                .works(responses)
                .page(workPage.getNumber())
                .size(workPage.getSize())
                .totalElements(workPage.getTotalElements())
                .totalPages(workPage.getTotalPages())
                .last(workPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public WorkResponse getWork(Long userId, Long workId) {

        // N+1 쿼리 개선: findByIdWithDetails() 사용 (4번 쿼리 → 1번 쿼리, 75% 개선)
        Work work = workRepository.findByIdWithDetails(workId)
                .orElseThrow(() -> new CustomException(WORK_NOT_FOUND));

        if (!work.getWorkflow().getUser().getId().equals(userId)) {
            throw new CustomException(NOT_WORKFLOW_OWNER);
        }

        AiContent aiContent = work.getAiContent();

        return WorkResponse.builder()
                .workId(work.getId())
                .postingUrl(work.getPostingUrl())
                .completedAt(work.getCompletedAt())
                .title(aiContent != null ? aiContent.getTitle() : null)
                .content(aiContent != null ? aiContent.getContent() : null)
                .choiceProduct(aiContent != null ? aiContent.getChoiceProduct() : null)
                .choiceTrendKeyword(aiContent != null ? aiContent.getChoiceTrendKeyword() : null)
                .status(work.getStatus())
                .build();
    }

    private Map<Long, String> fetchChoiceProductMap(List<Work> works) {
        List<Long> workIds = works.stream()
                .map(Work::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (workIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return aiContentRepository.findByWorkIdIn(workIds).stream()
                .filter(aiContent -> aiContent.getWork() != null)
                .collect(Collectors.toMap(
                        aiContent -> aiContent.getWork().getId(),
                        aiContent -> Optional.ofNullable(aiContent.getChoiceProduct()).orElse(""),
                        (existing, ignore) -> existing
                ));
    }

    private void validateWorkflowOwner(Workflow workflow, Long userId) {
        if (workflow.getUser() == null || workflow.getUser().getId() == null || !workflow.getUser().getId().equals(userId)) {
            throw new CustomException(NOT_WORKFLOW_OWNER);
        }
    }
}
