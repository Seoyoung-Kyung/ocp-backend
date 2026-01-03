package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.dto.response.AdminWorkflowListResponse;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.workflow.enums.SiteUrlInfo;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.*;
import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminWorkflowServiceImpl implements AdminWorkflowService {

    private final WorkflowRepository workflowRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminWorkflowListResponse> getWorkflows(UserPrincipal principal, int page, Long userId) {

        validateAndGetUser(principal);

        PageRequest pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Workflow> workflows;

        if (userId != null) {
            workflows = workflowRepository.findWorkflowsForAdminByUserId(userId, pageable);
        } else {
            workflows = workflowRepository.findWorkflowsForAdmin(pageable);
        }

        return workflows.map(this::toAdminWorkflowListResponse);
    }

    private void validateAndGetUser(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        User user = userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if(user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ACCESS_DENIED);
        }

    }

    private AdminWorkflowListResponse toAdminWorkflowListResponse(Workflow wf) {
        return AdminWorkflowListResponse.builder()
                .workflowId(wf.getId())
                .userId(wf.getUser().getId())
                .userName(wf.getUser().getName())
                .siteUrl(wf.getSiteUrl())
                .siteName(SiteUrlInfo.getSiteNameFromUrl(wf.getSiteUrl()))
                .trendCategoryName(wf.getTrendCategory().getTrendCategoryName())
                .blogType(wf.getUserBlog().getBlogType().getBlogTypeName())
                .blogAccountId(wf.getUserBlog().getAccountId())
                .blogUrl(wf.getUserBlog().getBlogUrl())
                .readableRule(wf.getRecurrenceRule().getReadableRule())
                .status(wf.getStatus())
                .testStatus(wf.getTestStatus())
                .build();
    }

}