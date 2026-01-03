package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.monitoring.dto.response.AdminWorkDetailLogResponse;
import com.ocp.ocp_finalproject.monitoring.service.WorkDetailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/work")
public class AdminWorkLogController {

    private final WorkDetailLogService logService;

    @GetMapping("/{workId}/logs")
    public ResponseEntity<ApiResult<List<AdminWorkDetailLogResponse>>> getAdminWorkLogs(
            @PathVariable Long workId
    ) {
        List<AdminWorkDetailLogResponse> logs = logService.getAdminLogs(workId);
        return ResponseEntity.ok(ApiResult.success("작업 로그 조회 성공(관리자)", logs));
    }
}