package com.ocp.ocp_finalproject.admin.dto.response;

import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserSuspension;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "계정 상태", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "권한", example = "USER")
    private UserRole role;

    @Schema(description = "생성일시", example = "2025-12-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-12-03T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "정지 정보")
    private SuspensionInfo suspensionInfo;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SuspensionInfo {
        @Schema(description = "정지 사유", example = "부적절한 콘텐츠 게시")
        private String reason;

        @Schema(description = "정지 일시", example = "2025-12-02T14:30:00")
        private LocalDateTime suspendedAt;

        @Schema(description = "정지 해제 일시", example = "2025-12-03T09:15:00")
        private LocalDateTime unsuspendedAt;

        @Schema(description = "활성 여부", example = "true")
        private Boolean isActive;

    }

    public static AdminUserResponse from(User user, UserSuspension suspension) {
        SuspensionInfo suspensionInfo = null;
        if (suspension != null) {
            suspensionInfo = SuspensionInfo.builder()
                    .reason(suspension.getReason())
                    .suspendedAt(suspension.getSuspendedAt())
                    .unsuspendedAt(suspension.getUnsuspendedAt())
                    .isActive(suspension.getIsActive())
                    .build();
        }

        return AdminUserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .suspensionInfo(suspensionInfo)
                .build();
    }

    public static AdminUserResponse fromWithoutSuspension(User user) {
        return from(user, null);
    }
}
