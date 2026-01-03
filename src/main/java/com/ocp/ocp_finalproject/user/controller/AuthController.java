package com.ocp.ocp_finalproject.user.controller;

import com.ocp.ocp_finalproject.audit.annotation.Audit;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.dto.response.UserResponse;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import com.ocp.ocp_finalproject.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 세션기반 인증
 * OAuth 소셜로그인
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 정보 조회
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResult<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {

        log.info("현재 사용자 정보 조회 요청");

        User user = principal.getUser();
        AuthProvider provider = userService.getUserProvider(user);

        UserResponse response = UserResponse.from(user, provider);

        log.info("사용자 정보 조회 성공 - userId: {}",user.getId());
        return ResponseEntity.ok(
            ApiResult.success("사용자 상세 조회 성공",response)
        );
    }

    /**
     * 로그 아웃
     * POST /api/v1/auth/logout
     */
    @Audit(
            action = AuditAction.USER_LOGOUT,
            actorType = ActorType.USER,
            targetType = "USER"
    )
    @PostMapping("/logout")
    public ResponseEntity <ApiResult<Void>> logout(@AuthenticationPrincipal UserPrincipal principal ,HttpServletRequest request) {
        log.info("로그아웃 요청 - userId: {}",principal.getUser().getId());

        try{
            // 1. 사용자 상태를 INACTIVE로 변경
            User user = principal.getUser();
            userService.deactivateUser(user.getId());

            // 2. 세션 무효화
            HttpSession session = request.getSession(false);
            if(session != null) {
                session.invalidate();
            }

            return ResponseEntity.ok(
                    ApiResult.success("로그아웃 성공")
            );
        }catch (Exception e){
            log.error("로그아웃 처리 중 에러 발생",e);
            return ResponseEntity.status(500).body(
                    ApiResult.error("로그아웃 처리 중 오류가 발생했습니다.")
            );
        }
    }

    /**
     * 회원 탈퇴
     * DELETE /api/v1/auth/withdraw
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResult<Void>> withdraw(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        User user = principal.getUser();
        log.info("회원 작ㄹ퇴 요청 - userId: {}",user.getId());

        try{
            userService.withdrawUser(user.getId());
            HttpSession session = request.getSession(false);

            if(session != null) {
                session.invalidate();
            }

            return  ResponseEntity.ok(ApiResult.success("회원 탈퇴가 완료되었습니다."));
        }catch (Exception e){
            log.error("회원 탈퇴 처리 중 에러 발생 - userId: {}",user.getId(),e.getMessage());
            return ResponseEntity.status(500).body(
                    ApiResult.error("회원 탈퇴 처리 중 오류가 발생했습니다.")
            );
        }
    }
}
