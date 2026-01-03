package com.ocp.ocp_finalproject.admin.controller;

import com.ocp.ocp_finalproject.admin.domain.CommonCode;
import com.ocp.ocp_finalproject.admin.domain.CommonCodeGroup;
import com.ocp.ocp_finalproject.admin.dto.request.CommonCodeRequest;
import com.ocp.ocp_finalproject.admin.dto.response.CommonCodeResponse;
import com.ocp.ocp_finalproject.admin.service.CommonCodeGroupService;
import com.ocp.ocp_finalproject.admin.service.CommonCodeService;
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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "공통코드")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class CommonCodeController {

    private final CommonCodeService commonCodeService;
    private final CommonCodeGroupService commonCodeGroupService;

    // 공통 코드 생성
    @Operation(summary = "공통코드 생성", description = "새로운 공통코드를 생성합니다.")
    @PostMapping("/common-codes")
    public ResponseEntity<ApiResult<CommonCodeResponse>> createCode(
            @Valid @RequestBody CommonCodeRequest request){

        // 그룹 조회
        CommonCodeGroup group = commonCodeGroupService.getGroup(request.getGroupId());

        // 코드 생성
        CommonCode code = CommonCode.createBuilder()
                .codeId(request.getCodeId())
                .commonCodeGroup(group)
                .codeName(request.getCodeName())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive())
                .build();

        CommonCode saveCode = commonCodeService.createCode(code);
        CommonCodeResponse response = CommonCodeResponse.from(saveCode);
        return ResponseEntity.ok(ApiResult.success("공통코드 생성 완료", response));
    }

    // 공통 코드 단건 조회
    @Operation(summary = "공통코드 단건 조회", description = "그룹 ID와 코드 ID로 공통코드를 조회합니다.")
    @GetMapping("/common-codes/{groupId}/{codeId}")
    public ResponseEntity<ApiResult<CommonCodeResponse>> getCode(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @Parameter(description = "코드 ID") @PathVariable String codeId){

        CommonCode code = commonCodeService.getCode(codeId);
        CommonCodeResponse response = CommonCodeResponse.from(code);
        return ResponseEntity.ok(ApiResult.success("공통코드 조회 성공", response));
    }

    // 그룹별 코드 목록 조회(리스트)
    @Operation(summary = "그룹별 코드 목록 조회", description = "특정 그룹의 모든 코드를 정렬 순서대로 조회합니다.")
    @GetMapping("/common-codes/{groupId}")
    public ResponseEntity<ApiResult<List<CommonCodeResponse>>> getCodesByGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        List<CommonCode> codes = commonCodeService.getCodesByGroupId(groupId);
        List<CommonCodeResponse> response = codes.stream()
                .map(CommonCodeResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResult.success("그룹별 코드 목록 조회 성공", response));
    }

    // 그룹별 코드 목록 조회 (페이징)
    @Operation(summary = "그룹별 코드 목록 조회(페이징)", description = "특정 그룹의 코드를 페이징하여 조회합니다.")
    @GetMapping("/common-codes/{groupId}/paged")
    public ResponseEntity<ApiResult<Page<CommonCodeResponse>>> getPagedCode(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @ParameterObject Pageable pageable){

        Page<CommonCode> codes = commonCodeService.getCodesByGroupIdPaged(groupId, pageable);
        Page<CommonCodeResponse> responses = codes.map(CommonCodeResponse::from);
        return ResponseEntity.ok(ApiResult.success("그룹별 코드 목록 조회 성공", responses));
    }

    // 활성화된 코드만 조회
    @Operation(summary = "활성화된 코드 조회", description = "특정 그룹의 활성화된 코드만 조회합니다.")
    @GetMapping("/common-codes/{groupId}/active")
    public ResponseEntity<ApiResult<List<CommonCodeResponse>>> getActiveCodesByGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        List<CommonCode> codes = commonCodeService.getActiveCodesByGroupId(groupId);
        List<CommonCodeResponse> responses = codes.stream()
                .map(CommonCodeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResult.success("활성 코드 목록 조회 성공", responses));
    }

    // 공통코드 검색
    @Operation(summary = "공통코드 검색", description = "그룹, 코드명, 활성화 상태로 검색합니다.")
    @GetMapping("/common-codes/search")
    public ResponseEntity<ApiResult<Page<CommonCodeResponse>>> searchCode(
            @Parameter(description = "그룹 ID") @RequestParam(required = false) String groupId,
            @Parameter(description = "코드명") @RequestParam(required = false) String codeName,
            @Parameter(description = "활성화 여부") @RequestParam(required = false) Boolean isActive,
            @ParameterObject Pageable pageable){

            Page<CommonCode> codes = commonCodeService.searchCodes(groupId, codeName, isActive, pageable);
            Page<CommonCodeResponse> responses = codes.map(CommonCodeResponse::from);
            return ResponseEntity.ok(ApiResult.success("공통코드 검색 완료", responses));
    }

    // 코드 정보 수정
    @Operation(summary = "공통코드 수정", description = "공통코드 정보를 수정합니다.")
    @PutMapping("/common-codes/{groupId}/{codeId}")
    public ResponseEntity<ApiResult<CommonCodeResponse>> updateCode(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @Parameter(description = "코드 ID") @PathVariable String codeId,
            @Valid @RequestBody CommonCodeRequest request){

        commonCodeService.updateCode(
                codeId,
                request.getCodeName(),
                request.getDescription(),
                request.getSortOrder()
        );

        CommonCode updateCode = commonCodeService.getCode(codeId);
        CommonCodeResponse response = CommonCodeResponse.from(updateCode);
        return ResponseEntity.ok(ApiResult.success("공통코드 수정 완료", response));
    }

    // 코드 활성화
    @Operation(summary = "공통코드 활성화", description = "공통코드를 활성화합니다.")
    @PatchMapping("/common-codes/{groupId}/{codeId}/activate")
    public ResponseEntity<ApiResult<CommonCodeResponse>> activateCode(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @Parameter(description = "코드 ID")  @PathVariable String codeId){

        commonCodeService.activateCode(codeId);
        CommonCode code = commonCodeService.getCode(codeId);
        CommonCodeResponse response = CommonCodeResponse.from(code);
        return ResponseEntity.ok(ApiResult.success("공통코드 활성화 완료", response));
    }

    // 코드 비활성화
    @Operation(summary = "공통코드 비활성화", description = "공통코드를 비활성화합니다.")
    @PatchMapping("/common-codes/{groupId}/{codeId}/deactivate")
    public ResponseEntity<ApiResult<CommonCodeResponse>> deactivateCode(
            @Parameter(description = "그룹 ID") @PathVariable String groupId,
            @Parameter(description = "코드 ID") @PathVariable String codeId){

        commonCodeService.deactivateCode(codeId);
        CommonCode code = commonCodeService.getCode(codeId);
        CommonCodeResponse response = CommonCodeResponse.from(code);
        return ResponseEntity.ok(ApiResult.success("공통코드 비활성화 완료", response));
    }

    // 그룹별 코드 개수 조회
    @Operation(summary = "그룹별 코드 개수", description = "특정 그룹의 전체 코드 개수를 조회합니다.")
    @GetMapping("/common-codes/{groupId}/count")
    public ResponseEntity<ApiResult<Long>> getCodeCountByGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        long count = commonCodeService.countByGroupId(groupId);
        return ResponseEntity.ok(ApiResult.success("코드 개수 조회 성공", count));
    }

    // 그룹별 활성 코드 개수 조회
    @Operation(summary = "그룹별 활성 코드 개수", description = "특정 그룹의 활성화된 코드 개수를 조회합니다.")
    @GetMapping("/common-codes/{groupId}/count/active")
    public ResponseEntity<ApiResult<Long>> getActiveCodeCountByGroup(
            @Parameter(description = "그룹 ID") @PathVariable String groupId){

        long count = commonCodeService.countActiveByGroupId(groupId);
        return ResponseEntity.ok(ApiResult.success("활성 코드 개수 조회 성공", count));
    }
}
