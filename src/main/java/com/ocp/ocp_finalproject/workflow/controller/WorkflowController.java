package com.ocp.ocp_finalproject.workflow.controller;

import com.ocp.ocp_finalproject.audit.annotation.Audit;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.workflow.dto.request.WorkflowRegisterRequest;
import com.ocp.ocp_finalproject.workflow.dto.request.WorkflowStatusRequest;
import com.ocp.ocp_finalproject.workflow.dto.response.BlogTypeResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.GetWorkflowResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.SiteUrlResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.TrendCategoryResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowEditResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowListResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowStatusResponse;
import com.ocp.ocp_finalproject.workflow.enums.SiteUrlInfo;
import com.ocp.ocp_finalproject.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.UNAUTHORIZED;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * 워크플로우 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResult<Page<WorkflowListResponse>>> getWorkflows(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {

        Long userId = validateAndGetUserId(principal);

        Page<WorkflowListResponse> workflowList = workflowService.getWorkflows(userId, page);

        return ResponseEntity.ok(ApiResult.success("워크플로우 목록 조회 성공", workflowList));
    }

    /**
     * 워크플로우 상세 조회 (수정용)
     */
    @GetMapping("/{workflowId}/edit")
    public ResponseEntity<ApiResult<WorkflowEditResponse>> getWorkflowEdit(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId
    ) {

        Long userId = validateAndGetUserId(principal);

        WorkflowEditResponse workflow = workflowService.getWorkflowForEdit(workflowId, userId);

        return ResponseEntity.ok(ApiResult.success("워크플로우 상세 조회 성공(수정용)", workflow));
    }

    /**
     * 워크플로우 상세 조회 (조회용)
     */
    @GetMapping("/{workflowId}")
    public ResponseEntity<ApiResult<GetWorkflowResponse>> getWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId
    ) {

        Long userId = validateAndGetUserId(principal);

        GetWorkflowResponse workflow = workflowService.getWorkflow(workflowId, userId);

        return ResponseEntity.ok(ApiResult.success("워크플로우 상세 조회 성공(조회용)", workflow));
    }

    /**
     * 워크플로우 등록
     */
    @Audit(
            action = AuditAction.WORKFLOW_CREATE,
            actorType = ActorType.USER,
            targetType = "WORKFLOW",
            targetIdSpel = "#workflowId"
    )
    @PostMapping("/{workflowId}/register")
    public ResponseEntity<ApiResult<WorkflowResponse>> registerWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId,
            @RequestBody(required = false) WorkflowRegisterRequest request
    ) throws SchedulerException {
        Long userId = validateAndGetUserId(principal);
        Long replaceWorkflowId = request != null ? request.getReplaceWorkflowId() : null;
        WorkflowResponse workflow = workflowService.registerWorkflow(userId, workflowId, replaceWorkflowId);
        return ResponseEntity.ok(ApiResult.success("워크플로우 등록 성공", workflow));
    }

    /**
     * 워크플로우 상태 변경
     */
    @Audit(
            action = AuditAction.WORKFLOW_STATUS_CHANGE,
            actorType = ActorType.USER,
            targetType = "WORKFLOW",
            targetIdSpel = "#workflowId"
    )
    @PatchMapping("/{workflowId}/status")
    public ResponseEntity<ApiResult<WorkflowStatusResponse>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId,
            @RequestBody WorkflowStatusRequest workflowStatusRequest
    ) {
        Long userId = validateAndGetUserId(principal);

        WorkflowStatusResponse workflowStatus = workflowService.updateStatus(userId, workflowId, workflowStatusRequest.getNewStatus());

        return ResponseEntity.ok(ApiResult.success("워크플로우 상태 변경 성공", workflowStatus));
    }

    /**
     * 워크플로우 삭제(논리 삭제)
     */
    @DeleteMapping("/{workflowId}")
    public ResponseEntity<ApiResult<WorkflowStatusResponse>> deleteWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId
    ) {
        Long userId = validateAndGetUserId(principal);

        WorkflowStatusResponse workflowStatus = workflowService.deleteWorkflow(userId, workflowId);

        return ResponseEntity.ok(ApiResult.success("워크플로우 삭제 성공", workflowStatus));
    }

    /**
     * 트렌드 카테고리 조회(워크플로우 등록 페이지)
     */
    @GetMapping("/trend-category")
    public ResponseEntity<ApiResult<List<TrendCategoryResponse>>> getTrendCategories() {

        List<TrendCategoryResponse> trendCategories = workflowService.getTrendCategories();

        return ResponseEntity.ok(ApiResult.success("트렌드 카테고리 조회 성공", trendCategories));
    }

    /**
     * 블로그 타입 조회(워크플로우 등록 페이지)
     */
    @GetMapping("/blog-type")
    public ResponseEntity<ApiResult<List<BlogTypeResponse>>> getBlogTypes() {

        List<BlogTypeResponse> blogTypes = workflowService.getBlogTypes();

        return ResponseEntity.ok(ApiResult.success("블로그 타입 조회 성공", blogTypes));
    }

    private Long validateAndGetUserId(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        return principal.getUser().getId();
    }

    /**
     * 사이트 정보
     */
    @GetMapping("/site-info")
    public ResponseEntity<ApiResult<List<SiteUrlResponse>>> getSiteInfo() {

        List<SiteUrlResponse> siteList = Arrays.stream(SiteUrlInfo.values())
                .map(SiteUrlResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResult.success("사이트 목록 조회", siteList));
    }

}
