package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.response.AdminWorkListResponse;
import com.ocp.ocp_finalproject.work.dto.response.AdminWorkPageResponse;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.*;
import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class AdminWorkServiceImpl implements AdminWorkService {

    private final WorkflowRepository workflowRepository;
    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminWorkPageResponse getWorksForAdmin(UserPrincipal principal, Long workflowId, int page) {
        if (page < 0) {
            throw new CustomException(INVALID_INPUT_VALUE, "페이지 번호는 0 이상의 정수여야 합니다.");
        }

        validateAndGetUser(principal);

        PageRequest pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Work> workPage;

        if (workflowId != null) {

            workflowRepository.findById(workflowId)
                    .orElseThrow(() -> new CustomException(WORKFLOW_NOT_FOUND));

            workPage = workRepository.findByWorkflowIdForAdmin(workflowId, pageable);
        } else {
            workPage = workRepository.findAllForAdmin(pageable);
        }

        List<Work> works = workPage.getContent();

        List<AdminWorkListResponse> responses = works.stream()
                .map(work -> AdminWorkListResponse.builder()
                        .workId(work.getId())
                        .workflowId(work.getWorkflow().getId())
                        .userId(work.getWorkflow().getUser().getId())
                        .status(work.getStatus())
                        .postingUrl(work.getPostingUrl())
                        .completedAt(work.getCompletedAt())
                        .title(work.getAiContent().getTitle())
                        .content(work.getAiContent().getContent())
                        .choiceProduct(work.getAiContent().getChoiceProduct())
                        .choiceTrendKeyword(work.getAiContent().getChoiceTrendKeyword())
                        .failureReason(work.getFailureReason())
                        .build())
                .collect(Collectors.toList());

        return AdminWorkPageResponse.builder()
                .works(responses)
                .page(workPage.getNumber())
                .size(workPage.getSize())
                .totalElements(workPage.getTotalElements())
                .totalPages(workPage.getTotalPages())
                .last(workPage.isLast())
                .build();
    }

    private User validateAndGetUser(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        User user = userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if(user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ACCESS_DENIED);
        }

        return user;
    }
}
