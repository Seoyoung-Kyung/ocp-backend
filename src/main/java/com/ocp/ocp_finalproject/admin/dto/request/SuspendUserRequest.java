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
public class SuspendUserRequest {

    @Schema(description = "정지 사유", example = "부적절한 콘텐츠 게시")
    @NotBlank(message = "정지 사유는 필수입니다.")
    @Size(max = 500, message = "정지 사유는 500자를 초과할 수 없습니다.")
    private String reason;
}
