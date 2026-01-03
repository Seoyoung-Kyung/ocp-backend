package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.monitoring.dto.response.WorkDetailLogResponse;
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
@RequestMapping("/api/v1/work")
public class WorkLogController {

    private final WorkDetailLogService logService;

    @GetMapping("/{workId}/logs")
    public ResponseEntity<ApiResult<List<WorkDetailLogResponse>>> getWorkLogs(
            @PathVariable Long workId
    ) {
        List<WorkDetailLogResponse> logs = logService.getUserLogs(workId);
        return ResponseEntity.ok(ApiResult.success("작업 로그 조회 성공", logs));
    }
}