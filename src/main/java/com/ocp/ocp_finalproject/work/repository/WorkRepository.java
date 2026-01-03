package com.ocp.ocp_finalproject.work.repository;

import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.dto.response.WorkResponse;
import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    /**
     * BlogUploadService에서 work 조회 시 사용되는 메서드
     *
     */
    @Query("""
        SELECT w
        FROM Work w
        JOIN FETCH w.workflow wf
        JOIN FETCH wf.userBlog ub
        LEFT JOIN FETCH ub.blogType bt
        WHERE wf.id = :workflowId
          AND w.status = :status
    """)
    List<Work> findWorksWithBlog(
            @Param("workflowId") Long workflowId,
            @Param("status") WorkExecutionStatus status
    );

    Page<Work> findByWorkflowId(Long workflowId, Pageable pageable);

    @Query("""
        SELECT w
        FROM Work w
        JOIN FETCH w.workflow wf
        LEFT JOIN FETCH wf.user u
        JOIN FETCH w.aiContent ac
        WHERE wf.id = :workflowId
    """)
    Page<Work> findByWorkflowIdForAdmin(Long workflowId, Pageable pageable);

    @Query("""
        SELECT new com.ocp.ocp_finalproject.work.dto.response.WorkResponse(
            w.id,
            w.postingUrl,
            w.completedAt,
            ac.choiceProduct,
            ac.title,
            ac.content,
            ac.choiceTrendKeyword,
            w.status
        )
        FROM Work w
        JOIN w.workflow wf
        LEFT JOIN w.aiContent ac
        WHERE w.id = :workId
    """)
    WorkResponse findWork(Long workId);

    @Query("""
        SELECT w
        FROM Work w
        JOIN FETCH w.workflow wf
        LEFT JOIN FETCH wf.user u
        JOIN FETCH w.aiContent ac
    """)
    Page<Work> findAllForAdmin(Pageable pageable);

    Optional<Work> findTopByWorkflowIdOrderByCreatedAtDesc(Long workflowId);

}
