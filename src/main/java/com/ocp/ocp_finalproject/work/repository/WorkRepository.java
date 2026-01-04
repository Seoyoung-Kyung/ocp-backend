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

    // N+1 쿼리 발생으로 findByWorkflowIdWithAiContent()로 대체
    // Page<Work> findByWorkflowId(Long workflowId, Pageable pageable);

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

    /**
     * N+1 쿼리 개선: Work 상세 조회 (Workflow, User, AiContent 함께 로딩)
     * 사용처: WorkService.getWork()
     * 개선: 4번 쿼리 → 1번 쿼리 (75% 개선)
     */
    @Query("""
        SELECT w
        FROM Work w
        JOIN FETCH w.workflow wf
        JOIN FETCH wf.user u
        LEFT JOIN FETCH w.aiContent ac
        WHERE w.id = :workId
    """)
    Optional<Work> findByIdWithDetails(@Param("workId") Long workId);

    /**
     * N+1 쿼리 개선: Work 리스트 조회 (AiContent 함께 로딩)
     * 사용처: WorkService.getWorks()
     * 개선: 2번 쿼리 → 1번 쿼리 (50% 개선)
     */
    @Query("""
        SELECT w
        FROM Work w
        LEFT JOIN FETCH w.aiContent ac
        WHERE w.workflow.id = :workflowId
    """)
    Page<Work> findByWorkflowIdWithAiContent(@Param("workflowId") Long workflowId, Pageable pageable);

    /**
     * N+1 쿼리 개선: Work 조회 (Workflow 함께 로딩)
     * 사용처: Webhook Services (KeywordSelectWebhookService, ContentGenerateWebhookService, ProductSelectWebhookService)
     * 개선: 2번 쿼리 → 1번 쿼리 (50% 개선)
     */
    @Query("""
        SELECT w
        FROM Work w
        JOIN FETCH w.workflow wf
        WHERE w.id = :workId
    """)
    Optional<Work> findByIdWithWorkflow(@Param("workId") Long workId);

}
