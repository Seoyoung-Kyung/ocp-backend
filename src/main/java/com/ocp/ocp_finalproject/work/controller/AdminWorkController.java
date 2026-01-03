package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.work.dto.response.AdminWorkPageResponse;
import com.ocp.ocp_finalproject.work.service.AdminWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/work")
public class AdminWorkController {

    private final AdminWorkService workService;

    @GetMapping
    public ResponseEntity<ApiResult<AdminWorkPageResponse>> getAllWorks(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long workflowId,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {

        AdminWorkPageResponse workPage = workService.getWorksForAdmin(principal, workflowId, page);

        return ResponseEntity.ok(ApiResult.success("워크 목록 조회 성공(관리자)", workPage));
    }
}