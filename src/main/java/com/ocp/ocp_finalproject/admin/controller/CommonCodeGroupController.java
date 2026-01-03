package com.ocp.ocp_finalproject.admin.controller;

import com.ocp.ocp_finalproject.admin.domain.CommonCodeGroup;
import com.ocp.ocp_finalproject.admin.dto.request.CommonCodeGroupRequest;
import com.ocp.ocp_finalproject.admin.dto.response.CommonCodeGroupResponse;
import com.ocp.ocp_finalproject.admin.service.CommonCodeGroupService;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공통코드 그룹")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class CommonCodeGroupController {

    private final CommonCodeGroupService commonCodeGroupService;

    // 공통 코드 그룹 생성
    @Operation(summary = "공통코드 그룹 생성", description = "새로운 공통코드 그룹을 생성합니다.")
    @PostMapping("/common-codes/groups")
    public ResponseEntity<ApiResult<CommonCodeGroupResponse>> createGroup(
            @Valid @RequestBody CommonCodeGroupRequest request){

        CommonCodeGroup group = CommonCodeGroup.createBuilder()
                .groupId(request.getGroupId())
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .codes(null)
                .build();

        CommonCodeGroup savedGroup = commonCodeGroupService.createGroup(group);
        CommonCodeGroupResponse response = CommonCodeGroupResponse.fromWithoutCodes(savedGroup);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 생성 완료", response));
    }


    // 공통 코드 그룹 단건 조회
    @Operation(summary = "공통코드 그룹 단건 조회", description = "그룹 ID로 공통코드 그룹을 조회합니다.")
    @GetMapping("/common-codes/groups/{groupId}")
    public ResponseEntity<ApiResult<CommonCodeGroupResponse>> getGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        CommonCodeGroup group = commonCodeGroupService.getGroup(groupId);
        CommonCodeGroupResponse response = CommonCodeGroupResponse.fromWithoutCodes(group);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 조회 성공",response));
    }

    //공통코드 그룹 + 코드 조회 (N+1 방지)
    @Operation(summary = "공통코드 그룹 + 코드 조회", description = "그룹과 포함된 코드를 함께 조회합니다.")
    @GetMapping("/common-codes/groups/{groupId}/with-codes")
    public ResponseEntity<ApiResult<CommonCodeGroupResponse>> getGroupWithCodes(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        CommonCodeGroup group = commonCodeGroupService.getGroupWithCode(groupId);
        CommonCodeGroupResponse response = CommonCodeGroupResponse.from(group);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹(코드 포함) 조회 성공", response));
    }

    //공통코드 그룹 목록 조회(페이징)
    @Operation(summary = "공통코드 그룹 목록 조회", description = "모든 공통코드 그룹을 조회합니다. (페이징)")
    @GetMapping("/common-codes/groups")
    public ResponseEntity<ApiResult<Page<CommonCodeGroupResponse>>> getAllGroups(
            @ParameterObject Pageable pageable){

        Page<CommonCodeGroup> groups = commonCodeGroupService.getGroups(pageable);
        Page<CommonCodeGroupResponse> responses = groups.map(CommonCodeGroupResponse::fromWithoutCodes);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 목록 조회", responses));
    }

    //공통코드 그룹 검색
    @Operation(summary = "공통코드 그룹 검색", description = "그룹 ID나 그룹명으로 검색합니다.")
    @GetMapping("/common-codes/groups/search")
    public ResponseEntity<ApiResult<Page<CommonCodeGroupResponse>>> searchGroups(
            @Parameter(description = "그룹 ID") @RequestParam(required = false) String groupId,
            @Parameter(description = "그룹명") @RequestParam(required = false) String groupName,
            @ParameterObject Pageable pageable){

        Page<CommonCodeGroup> groups = commonCodeGroupService.searchGroups(groupId, groupName, pageable);
        Page<CommonCodeGroupResponse> responses = groups.map(CommonCodeGroupResponse::fromWithoutCodes);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 검색 완료", responses));
    }

    // 공통코드 그룹 정보 수정
    @Operation(summary = "공통코드 그룹 수정", description = "공통코드 그룹 정보를 수정합니다.")
    @PutMapping("/common-codes/groups/{groupId}")
    public ResponseEntity<ApiResult<CommonCodeGroupResponse>> updateGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @Valid @RequestBody  CommonCodeGroupRequest request){

        commonCodeGroupService.updateGroup(
                groupId,
                request.getGroupName(),
                request.getDescription()
        );

        CommonCodeGroup updateGroup = commonCodeGroupService.getGroup(groupId);
        CommonCodeGroupResponse response = CommonCodeGroupResponse.fromWithoutCodes(updateGroup);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 수정 완료", response));
    }

    //공통코드 그룹 삭제
    @Operation(summary = "공통코드 그룹 삭제", description = "공통코드 그룹을 삭제합니다. (포함된 코드도 함께 삭제)")
    @DeleteMapping("/common-codes/groups/{groupId}")
    public ResponseEntity<ApiResult<Void>> deleteGroup(
            @PathVariable String groupId){

        commonCodeGroupService.deleteGroup(groupId);
        return ResponseEntity.ok(ApiResult.success("공통코드 그룹 삭제 완료"));
    }
}