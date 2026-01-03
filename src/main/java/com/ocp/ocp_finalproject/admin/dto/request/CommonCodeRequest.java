package com.ocp.ocp_finalproject.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeRequest {

    @Schema(description = "코드 ID", example = "ACTIVE")
    @NotBlank(message = "코드 ID는 필수입니다.")
    @Size(max = 50, message = "코드 ID는 50자를 초과할 수 없습니다.")
    private String codeId;

    @Schema(description = "코드 그룹 ID", example = "USER_STATUS")
    @NotBlank(message = "코드 그룹 ID는 필수입니다.")
    @Size(max = 50, message = "코드 그룹 ID는 50자를 초과할 수 없습니다.")
    private String groupId;

    @Schema(description = "코드명", example = "활성")
    @NotBlank(message = "코드 명은 필수입니다.")
    @Size(max = 100, message = "코드명은 100자를 초과할 수 없습니다.")
    private String codeName;

    @Schema(description = "설명", example = "정상적으로 활동 중인 사용자")
    @Size(max = 255, message = "설명은 255자를 초과할 수 없습니다.")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "활성화 여부", example = "true")
    @NotNull(message = "활성화 여부는 필수입니다.")
    private Boolean isActive;
}
