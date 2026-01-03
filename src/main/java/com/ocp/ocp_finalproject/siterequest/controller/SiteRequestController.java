package com.ocp.ocp_finalproject.siterequest.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.siterequest.dto.request.SiteRequestCreateDto;
import com.ocp.ocp_finalproject.siterequest.dto.response.SiteRequestResponse;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import com.ocp.ocp_finalproject.siterequest.service.SiteRequestService;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/site-requests")
@RequiredArgsConstructor
public class SiteRequestController {

    private final SiteRequestService siteRequestService;

    /**
     * 사이트 요청 생성
     */
    @PostMapping
    public ResponseEntity<ApiResult<SiteRequestResponse>> createSiteRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SiteRequestCreateDto request
    ) {
        SiteRequestResponse response = siteRequestService.createRequest(
                principal.getUser().getId(),
                request
        );

        return ResponseEntity.ok(ApiResult.success("사이트 요청이 성공적으로 등록되었습니다", response));
    }

    /**
     * 내 요청 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResult<Page<SiteRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Page<SiteRequestResponse> requests = siteRequestService.getMyRequests(
                principal.getUser().getId(),
                page
        );

        return ResponseEntity.ok(ApiResult.success("내 사이트 요청 목록 조회 성공", requests));
    }
}