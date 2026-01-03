package com.ocp.ocp_finalproject.admin.dto.response;

import com.ocp.ocp_finalproject.admin.domain.CommonCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeResponse {
    @Schema(description = "코드 ID", example = "ACTIVE")
    private String codeId;

    @Schema(description = "코드 그룹 ID", example = "USER_STATUS")
    private String groupId;

    @Schema(description = "코드명", example = "활성")
    private String codeName;

    @Schema(description = "설명", example = "정상적으로 활동 중인 사용자")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "생성일시", example = "2025-12-03T10:00:00")
    private LocalDateTime createAt;

    @Schema(description = "수정일시", example = "2025-12-03T10:00:00")
    private LocalDateTime updateAt;

    public static CommonCodeResponse from(CommonCode commonCode) {
        return CommonCodeResponse.builder()
                .codeId(commonCode.getId())
                .groupId(commonCode.getCommonCodeGroup().getId())
                .codeName(commonCode.getCodeName())
                .description(commonCode.getDescription())
                .sortOrder(commonCode.getSortOrder())
                .isActive(commonCode.getIsActive())
                .createAt(commonCode.getCreatedAt())
                .updateAt(commonCode.getUpdatedAt())
                .build();
    }
}
