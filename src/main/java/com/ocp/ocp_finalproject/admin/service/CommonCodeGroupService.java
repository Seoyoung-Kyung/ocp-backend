package com.ocp.ocp_finalproject.admin.service;

import com.ocp.ocp_finalproject.admin.domain.CommonCodeGroup;
import com.ocp.ocp_finalproject.admin.repository.CommonCodeGroupRepository;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class CommonCodeGroupService {
    private final CommonCodeGroupRepository commonCodeGroupRepository;

    /*
    * 그룹 생성
    * @Param group 공통코드 그룹
    * @return 생성된 그룹
    * */
    @Transactional
    public CommonCodeGroup createGroup(CommonCodeGroup group){
        // 그룹 ID 중복 체크
        if(commonCodeGroupRepository.existsById(group.getId())){
            throw new CustomException(ErrorCode.DUPLICATE_COMMON_CODE);
        }
        return commonCodeGroupRepository.save(group);
    }

    /*
    * 그룹 단건 조회
    * @Param groupId 그룹 ID
    * @return 공통코드 그룹
    * */
    public CommonCodeGroup getGroup(String groupId){
        return commonCodeGroupRepository.findById(groupId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND));
    }

    /*
    * 그룹 목록 조회(페이징)
    * @Param pageable 페이징 정보
    * @return 그룹 목록
    * */
    public Page<CommonCodeGroup> getGroups(Pageable pageable){
        return commonCodeGroupRepository.findAll(pageable);
    }

    /*
    * 그룹 검색
    * @Param groupId 그룹 ID (선택)
    * @Param groupName 그룹명 (선택)
    * @Param pageable 페이징 정보
    * @return 그룹 목록
    * */
    public Page<CommonCodeGroup> searchGroups(String groupId, String groupName, Pageable pageable){
        return commonCodeGroupRepository.searchGroup(groupId, groupName, pageable);
    }

    /*
    * 그룹명으로 검색
    * @Param groupName 그룹명
    * @Param pageable 페이징 정보
    * @return 그룹 목록
    * */
    public Page<CommonCodeGroup> searchByGroups(String groupName, Pageable pageable){
        return commonCodeGroupRepository.findByGroupNameContaining(groupName, pageable);
    }

    /*
    * 코드 포함 그룹 조회(N+1 방지)
    * @Param groupId 그룹 ID
    * @return 공통코드 그룹 (코드 포함)
    * */
    public CommonCodeGroup getGroupWithCode(String groupId){
        return commonCodeGroupRepository.findByIdWithCodes(groupId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND));
    }

    /*
    * 그룹 정보 수정
    * @Param groupId 그룹 ID
    * @Param groupName 그룹명
    * @Param description 설명
    * */
    @Transactional
    public void updateGroup(String groupId, String groupName, String description){
        CommonCodeGroup group = getGroup(groupId);
        group.updateInfo(groupName, description);
    }

    /*
    * 그룹 삭제
    * @Param groupId 그룹 ID
    * */
    @Transactional
    public void deleteGroup(String groupId){
        CommonCodeGroup group = getGroup(groupId);
        commonCodeGroupRepository.delete(group);
    }

}
