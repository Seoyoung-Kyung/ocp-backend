package com.ocp.ocp_finalproject.siterequest.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.siterequest.dto.response.SiteRequestResponse;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import com.ocp.ocp_finalproject.siterequest.service.SiteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/site-requests")
@RequiredArgsConstructor
public class SiteRequestAdminController {

    private final SiteRequestService siteRequestService;

    /**
     * 전체 요청 목록 조회 (관리자용)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<Page<SiteRequestResponse>>> getAllRequests(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "state", required = false) SiteRequestState state
    ) {
        Page<SiteRequestResponse> requests = siteRequestService.getAllRequests(page, state);

        return ResponseEntity.ok(ApiResult.success("전체 사이트 요청 목록 조회 성공", requests));
    }

    /**
     * 사이트 요청 승인 (관리자용)
     */
    @PatchMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<SiteRequestResponse>> approveRequest(
            @PathVariable Long requestId
    ) {
        SiteRequestResponse response = siteRequestService.approveRequest(requestId);

        return ResponseEntity.ok(ApiResult.success("사이트 요청이 승인되었습니다", response));
    }

    /**
     * 사이트 요청 거부 (관리자용)
     */
    @PatchMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<SiteRequestResponse>> rejectRequest(
            @PathVariable Long requestId
    ) {
        SiteRequestResponse response = siteRequestService.rejectRequest(requestId);

        return ResponseEntity.ok(ApiResult.success("사이트 요청이 거부되었습니다", response));
    }
}
