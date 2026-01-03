package com.ocp.ocp_finalproject.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupRequest {

    @Schema(description = "코드 그룹 ID", example = "USER_STATUS")
    @NotBlank(message = "코드 그룹 ID는 필수입니다.")
    @Size(max = 50, message = "코드 그룹 ID는 50자를 초과할 수 없습니다.")
    private String groupId;

    @Schema(description = "코드 그룹명", example = "사용자 상태")
    @NotBlank(message = "코드 그룹명은 필수입니다.")
    @Size(max = 100, message = "코드 그룹명은 100자를 초과할 수 없습니다.")
    private String groupName;

    @Schema(description = "설명", example = "사용자 계정의 상태를 나타내는 코드 그룹")
    @Size(max = 255, message = "설명은 255자를 초과할 수 없습니다.")
    private String description;
}
