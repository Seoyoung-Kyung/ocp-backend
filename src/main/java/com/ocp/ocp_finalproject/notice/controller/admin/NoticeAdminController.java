package com.ocp.ocp_finalproject.notice.controller.admin;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.notice.dto.request.NoticeCreateRequest;
import com.ocp.ocp_finalproject.notice.dto.request.NoticeUpdateRequest;
import com.ocp.ocp_finalproject.notice.dto.response.NoticeResponse;
import com.ocp.ocp_finalproject.notice.service.NoticeService;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notices")
public class NoticeAdminController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회 (관리자용)
     */
    @GetMapping
    public ResponseEntity<ApiResult<List<NoticeResponse>>> getAllNotices() {
        return ResponseEntity.ok(ApiResult.success("공지사항 목록 조회 성공", noticeService.getAllNotice()));
    }

    /**
     * 공지사항 등록
     */
    @PostMapping
    public ResponseEntity<ApiResult<NoticeResponse>> createNotice(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NoticeCreateRequest request
    ) {
        // principal null 체크
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ApiResult.error("인증이 필요합니다"));
        }

        Long authorId = principal.getUser().getId();
        return ResponseEntity.ok(
                ApiResult.success("공지사항 등록 성공", noticeService.createNotice(request, authorId))
        );
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResult<Void>> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResult.success("공지사항 삭제 성공", null)
        );
    }
    /**
     * 공지사항 수정
     */
    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResult<NoticeResponse>> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResult.success("공지사항 수정 성공", noticeService.updateNotice(noticeId, request))
        );
    }
}
