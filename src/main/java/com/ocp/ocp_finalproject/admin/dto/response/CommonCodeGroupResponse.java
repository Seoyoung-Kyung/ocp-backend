package com.ocp.ocp_finalproject.admin.dto.response;

import com.ocp.ocp_finalproject.admin.domain.CommonCodeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupResponse {
    @Schema(description = "코드 그룹 ID", example = "USER_STATUS")
    private String groupId;

    @Schema(description = "코드 그룹명", example = "사용자 상태")
    private String groupName;

    @Schema(description = "설명", example = "사용자 계정의 상태를 나타내는 코드 그룹")
    private String description;

    @Schema(description = "생성일시", example = "2025-12-03T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-12-03T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "포함된 공통코드 목록")
    private List<CommonCodeResponse> commonCodes;

    public static CommonCodeGroupResponse from(CommonCodeGroup group) {
        return CommonCodeGroupResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .commonCodes(group.getCommonCodes().stream()
                        .map(CommonCodeResponse::from)
                        .collect(Collectors.toList()))
                .build();

                /*   <stream을 for문으로 작성한다면?>
                 *   List<CommonCodeResponse> responses = new ArrayList<>();
                 *   for (CommonCode code : group.getCommonCodes()) {
                 *       responses.add(CommonCodeResponse.from(code));
                 *       }
                 */
    }
    public static CommonCodeGroupResponse fromWithoutCodes(CommonCodeGroup group) {
        return CommonCodeGroupResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
