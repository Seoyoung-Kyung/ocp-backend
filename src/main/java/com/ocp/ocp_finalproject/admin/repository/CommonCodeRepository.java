package com.ocp.ocp_finalproject.admin.repository;

import com.ocp.ocp_finalproject.admin.domain.CommonCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {

    /*
    * 그룹 ID로 코드 목록 조회
    * @Param groupId 그룹 ID
    * @return 코드 목록
    * */

    @Query("SELECT c FROM CommonCode c WHERE c.commonCodeGroup.id= :groupId ORDER BY c.sortOrder ASC")
    List<CommonCode> findByGroupId(@Param("groupId") String groupId);

    /*
    * 그룹 ID로 코드 목록 조회 (페이징)
    * @Param groupId 그룹 ID
    * @Param pageable 페이징 정보
    * @return 코드 목록
    * */
    @Query("SELECT c FROM CommonCode c WHERE c.commonCodeGroup.id = :groupId")
    Page<CommonCode> findByGroupIdPaged(@Param("groupId") String groupId, Pageable pageable);


    /*
    * 활성화된 코드만 조회
    * @Param groupId 그룹 ID
    * @return 활성 코드 목록
    * */
    @Query("SELECT c FROM CommonCode c " +
            "WHERE c.commonCodeGroup.id = :groupId " +
            "AND c.isActive = true " +
            "ORDER BY c.sortOrder ASC")
    List<CommonCode> findActiveCodesByGroupId(@Param("groupId") String groupId);

    /*
    * 코드명으로 검색
    * @Param CodeName 코드명
    * @return 코드 목록
    * */
    Page<CommonCode> findByCodeNameContaining(String codeName, Pageable pageable);

    /*
    * 활성화 상태로 필터링
    * @Param isActive 활성화 여부
    * @Param Pageable 페이징 정보
    * @return 코드 목록
    * */
    Page<CommonCode> findByIsActive(Boolean isActive, Pageable pageable);

    /*
    * 복합 검색 (그룹, 코드명, 활성화 상태)
    * @Param groupId 그룹 ID
    * @Param codeName 코드명
    * @Param isActive 활성화 여부
    * @Param pageable 페이징 정보
    * return 코드 목록
    * */
    @Query("SELECT c FROM CommonCode c WHERE " +
            "(:groupId IS NULL OR c.commonCodeGroup.id = :groupId) AND " +
            "(:codeName IS NULL OR c.codeName LIKE %:codeName%) AND " +
            "(:isActive IS NULL OR c.isActive = :isActive)")
    Page<CommonCode> searchCodes(
            @Param("groupId") String groupId,
            @Param("codeName") String codeName,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
    /*
    * 그룹별 코드 개수
    * @param groupId 그룹 ID
    * @return 코드 개수
    * */
    @Query("SELECT COUNT(c) FROM CommonCode c WHERE " +
            "c.commonCodeGroup.id = :groupId")
    long countByGroupId(@Param("groupId") String groupId);

    /*
    * 활성화된 코드 개수
    * @param groupId 그룹 ID
    * @return 활성 코드 개수
    */
    @Query("SELECT COUNT(c) FROM CommonCode c WHERE " +
            "c.commonCodeGroup.id = :groupId AND " +
            "c.isActive = true")
    long countActiveByGroupId(@Param("groupId") String groupId);

}
