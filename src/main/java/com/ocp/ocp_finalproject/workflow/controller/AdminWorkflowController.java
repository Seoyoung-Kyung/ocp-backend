package com.ocp.ocp_finalproject.workflow.controller;

import com.ocp.ocp_finalproject.workflow.dto.response.AdminWorkflowListResponse;
import com.ocp.ocp_finalproject.workflow.service.AdminWorkflowService;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/workflow")
public class AdminWorkflowController {

    private final AdminWorkflowService workflowService;

    /**
     * 워크플로우 목록 조회(관리자)
     */
    @GetMapping
    public ResponseEntity<ApiResult<Page<AdminWorkflowListResponse>>> getWorkflows(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(required = false) Long userId
    ) {

        Page<AdminWorkflowListResponse> workflowList = workflowService.getWorkflows(principal, page, userId);

        return ResponseEntity.ok(ApiResult.success("워크플로우 목록 조회 성공(관리자)", workflowList));
    }

}