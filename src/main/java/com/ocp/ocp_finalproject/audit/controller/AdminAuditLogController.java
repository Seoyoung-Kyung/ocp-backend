package com.ocp.ocp_finalproject.audit.controller;

import com.ocp.ocp_finalproject.audit.dto.response.AuditLogResponse;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import com.ocp.ocp_finalproject.audit.service.AdminAuditLogService;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminAuditLogService adminAuditLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResult<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String actorType,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditResult result,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        ActorType resolvedActorType = null;
        if (actorType != null && !actorType.isBlank()) {
            try {
                resolvedActorType = ActorType.valueOf(actorType.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "actorType 파라미터가 올바르지 않습니다.");
            }
        }
        Page<AuditLogResponse> logs = adminAuditLogService.search(
                resolvedActorType,
                action,
                result,
                targetType,
                actorId,
                from,
                to,
                page,
                size
        );
        return ApiResult.success("감사 로그 조회 성공", logs);
    }
}
