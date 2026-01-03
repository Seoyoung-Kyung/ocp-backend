package com.ocp.ocp_finalproject.admin.controller;

import com.ocp.ocp_finalproject.admin.dto.request.SuspendUserRequest;
import com.ocp.ocp_finalproject.admin.dto.response.AdminUserResponse;
import com.ocp.ocp_finalproject.audit.annotation.Audit;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.admin.service.AdminUserService;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.user.domain.UserSuspension;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "관리자 - 사용자 관리")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "전체 사용자 조회", description = "모든 사용자를 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 사용자 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiResult<Page<AdminUserResponse>>> getAllUsers(
            @ParameterObject Pageable pageable){

        Page<User> users = adminUserService.getAllUsers(pageable);
        Page<AdminUserResponse> responses = users.map(AdminUserResponse::fromWithoutSuspension);
        return ResponseEntity.ok(ApiResult.success("전체 사용자 조회 성공", responses));
    }

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다. (정지 이력 포함)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResult<AdminUserResponse>> getUserDetails(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {

        User user = adminUserService.getUserById(userId);
        UserSuspension suspension = adminUserService.getActiveSuspension(user);
        AdminUserResponse response = AdminUserResponse.from(user, suspension);
        return ResponseEntity.ok(ApiResult.success("사용자 상세 조회 성공", response));
    }

    @Operation(summary = "사용자 활동 정지", description = "특정 사용자의 활동을 정지합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 활동 정지 완료"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @Audit(
            action = AuditAction.ADMIN_USER_SUSPEND,
            actorType = ActorType.ADMIN,
            targetType = "USER",
            targetIdSpel = "#userId"
    )
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResult<Void>> suspendUser(
            @Parameter(description = "사용자 ID", example = "5") @PathVariable Long userId,
            @Valid @RequestBody SuspendUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long adminUserId = principal.getUser().getId();
        adminUserService.suspendUser(userId, adminUserId, request.getReason());
        return ResponseEntity.ok(ApiResult.success("사용자 활동 정지 완료"));
    }

    @Operation(summary = "사용자 활동 정지 해제", description = "정지된 사용자의 활동을 재개합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 활동 정지 해제 완료"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/users/{userId}/unsuspend")
    public ResponseEntity<ApiResult<Void>> unsuspendUser(
            @Parameter(description = "사용자 ID", example = "5") @PathVariable Long userId) {

        adminUserService.unsuspendUser(userId);
        return ResponseEntity.ok(ApiResult.success("사용자 활동 정지 해제 완료"));
    }

    @Operation(summary = "사용자 검색", description = "이름, 이메일, 상태, 권한으로 사용자를 검색합니다. 모든 파라미터는 선택 사항입니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users/search")
    public ResponseEntity<ApiResult<Page<AdminUserResponse>>> searchUsers(
            @Parameter(description = "이름 (부분 검색)", example = "홍길동")
            @RequestParam(required = false) String name,

            @Parameter(description = "이메일 (부분 검색)", example = "hong@example.com")
            @RequestParam(required = false) String email,

            @Parameter(description = "계정 상태 (ACTIVE, INACTIVE, SUSPENDED, DORMANT, WITHDRAWN)", example = "SUSPENDED",
                       schema = @Schema(implementation = UserStatus.class))
            @RequestParam(required = false) UserStatus status,

            @Parameter(description = "권한 (ADMIN, USER, GUEST)", example = "USER",
                       schema = @Schema(implementation = UserRole.class))
            @RequestParam(required = false) UserRole role,

            @ParameterObject Pageable pageable) {

        Page<User> users = adminUserService.searchUsers(name, email, status, role, pageable);
        Page<AdminUserResponse> responses = users.map(AdminUserResponse::fromWithoutSuspension);
        return ResponseEntity.ok(ApiResult.success("사용자 검색 성공", responses));
    }
}
