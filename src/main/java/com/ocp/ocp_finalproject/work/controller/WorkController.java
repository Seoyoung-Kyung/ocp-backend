package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.work.dto.response.WorkPageResponse;
import com.ocp.ocp_finalproject.work.dto.response.WorkResponse;
import com.ocp.ocp_finalproject.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/work")
public class WorkController {

    private final WorkService workService;

    @GetMapping("/{workflowId}")
    public ResponseEntity<ApiResult<WorkPageResponse>> getAllWorks(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("workflowId") Long workflowId,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        Long userId = principal.getUser().getId();
        WorkPageResponse workPage = workService.getWorks(userId, workflowId, page);

        return ResponseEntity.ok(ApiResult.success("워크 목록 조회 성공", workPage));
    }

    @GetMapping("/find/{workId}")
    public ResponseEntity<ApiResult<WorkResponse>> getWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("workId") Long workId
    ) {
        if (principal == null || principal.getUser() == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        Long userId = principal.getUser().getId();
        WorkResponse work = workService.getWork(userId, workId);

        return ResponseEntity.ok(ApiResult.success("워크 단일 조회 성공", work));
    }
}
