package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.workflow.dto.response.AdminWorkflowListResponse;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import org.springframework.data.domain.Page;

public interface AdminWorkflowService {

    Page<AdminWorkflowListResponse> getWorkflows(UserPrincipal principal, int page, Long userId);
}