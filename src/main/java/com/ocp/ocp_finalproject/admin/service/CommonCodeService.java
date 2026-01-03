package com.ocp.ocp_finalproject.admin.service;

import com.ocp.ocp_finalproject.admin.domain.CommonCode;
import com.ocp.ocp_finalproject.admin.repository.CommonCodeRepository;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonCodeService {
    private final CommonCodeRepository commonCodeRepository;

    /*
     * 코드 생성
     * @Param code 공통코드
     * @return 생성된 코드
     * */
    @Transactional
    public CommonCode createCode(CommonCode code) {
        String codeId = code.getId();

        if (commonCodeRepository.existsById(codeId)) {
             throw new CustomException(ErrorCode.DUPLICATE_COMMON_CODE);
        }

        return commonCodeRepository.save(code);
    }

    /*
     * 코드 단건 조회
     * @Param codeId 코드 ID
     * @return 공통코드
     * */
    public CommonCode getCode(String codeId) {
        return commonCodeRepository.findById(codeId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMON_CODE_NOT_FOUND));
    }

    /*
     * 그룹 ID로 코드 목록 조회
     * @Param groupId 그룹 ID
     * @return 코드 목록 (정렬됨)
     * */
    public List<CommonCode> getCodesByGroupId(String groupId) {
        return commonCodeRepository.findByGroupId(groupId);
    }

    /*
     * 그룹 ID로 코드 목록 조회 (페이징)
     * @Param groupId 그룹 ID
     * @Param pageable 페이징 정보
     * @return 코드 목록
     * */
    public Page<CommonCode> getCodesByGroupIdPaged(String groupId, Pageable pageable) {
        return commonCodeRepository.findByGroupIdPaged(groupId, pageable);
    }

    /*
     * 활성화된 코드만 조회
     * @Param groupId 그룹 ID
     * @return 활성 코드 목록
     * */
    public List<CommonCode> getActiveCodesByGroupId(String groupId) {
        return commonCodeRepository.findActiveCodesByGroupId(groupId);
    }

    /*
    * 코드명으로 검색
    * @Param codeName 코드명
    * @Param pageable 페이징 정보
    * @return 코드 목록
    * */
    public Page<CommonCode> searchByCodeName(String codeName, Pageable pageable) {
        return commonCodeRepository.findByCodeNameContaining(codeName, pageable);
    }

    /*
    * 활성화 상태로 필터링
    * @Param isActive 활성화 여부
    * @Param pageable 페이징 정보
    * @return 코드 목록
    * */
    public Page<CommonCode> getCodeByActiveStatus(Boolean isActive, Pageable pageable) {
        return commonCodeRepository.findByIsActive(isActive, pageable);
    }

    /*
    * @Param groupId 그룹 ID (선택)
    * @Param codeName 코드명 (선택)
    * @Param isActive 활성화 여부 (선택)
    * @Param pageable 페이징 정보
    * @return 코드 목록
    * */
    public Page<CommonCode> searchCodes(String groupId, String codeName, Boolean isActive, Pageable pageable){
        return commonCodeRepository.searchCodes(groupId, codeName, isActive, pageable);
    }

    /*
    * 그룹별 코드 개수
    * @Param groupId 그룹 ID
    * @return 코드 개수
    * */
    public long countByGroupId(String groupId) {
        return commonCodeRepository.countByGroupId(groupId);
    }

    /*
    * 활성화된 코드 개수
    * @Param groupdId 그룹 ID
    * @return 활성 코드 개수
    * */
    public long countActiveByGroupId(String groupId) {
        return commonCodeRepository.countActiveByGroupId(groupId);
    }

    /*
    * 코드 비활성화
    * @Param codeId 코드 ID
    * */
    @Transactional
    public void deactivateCode(String codeId)
    {
        CommonCode code = getCode(codeId);
        code.updateActive(false);
    }

    /*
    * 코드 활성화
    * @Param codeId 코드 ID
    * */
    @Transactional
    public void activateCode(String codeId)
    {
        CommonCode code = getCode(codeId);
        code.updateActive(true);
    }

    /*
    * 코드 정보 수정
    * @Param codeId 코드 ID
    * @Param codeName 코드명
    * @Param description 설명
    * @Param sortOrder 정렬 순서
    * */
    @Transactional
    public void updateCode(String codeId, String codeName, String description, Integer sortOrder)
    {
        CommonCode code = getCode(codeId);
        code.updateInfo(codeName, description, sortOrder);
    }
}
