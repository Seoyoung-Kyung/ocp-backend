package com.ocp.ocp_finalproject.admin.repository;

import com.ocp.ocp_finalproject.admin.domain.CommonCodeGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroup, String> {

    /*
    * 그룹명으로 검색 (페이징)
    * @param groupName 그룹명
    * @param pageable 페이징 정보
    * @return 그룹 목록
    * */
    Page<CommonCodeGroup> findByGroupNameContaining(String groupName, Pageable pageable);

    /*
    * 그룹 ID 또는 그룹명으로 검색
    * @param groupId 그룹 ID
    * @param groupName 그룹명
    * @param pageable 페이징 정보
    * @return 그룹 목록
    * */
    @Query("SELECT g FROM CommonCodeGroup g WHERE "+
            "(:groupId IS NULL OR g.id LIKE %:groupId%) AND " +
            "(:groupName IS NULL or g.groupName LIKE %:groupName%)")
    Page<CommonCodeGroup> searchGroup(
            @Param("groupId") String groupId,
            @Param("groupName") String groupName,
            Pageable pageable
    );

    /*
    * 코드 포함 그룹 조회 (N+1 방지)
    * @param groupId 그룹 ID
    * @return 공통코드 그룹
    * */
    @Query("SELECT g FROM CommonCodeGroup g " +
            "LEFT JOIN FETCH g.commonCodes " +
            "WHERE g.id = :groupId")
    Optional<CommonCodeGroup> findByIdWithCodes(@Param("groupId") String groupId);
}
