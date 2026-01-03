package com.ocp.ocp_finalproject.workflow.repository;

import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.dto.response.AdminWorkflowListResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    @Query("""
            SELECT new com.ocp.ocp_finalproject.workflow.dto.response.WorkflowListResponse(
                wf.id,
                u.id,
                wf.siteUrl,
                bt.blogTypeName,
                ub.blogUrl,
                tc.trendCategoryName,
                ub.accountId,
                rr.readableRule,
                wf.status,
                wf.testStatus
            )
            FROM Workflow wf
            JOIN wf.user u
            JOIN wf.trendCategory tc
            LEFT JOIN wf.recurrenceRule rr
            JOIN wf.userBlog ub
            LEFT JOIN ub.blogType bt
            WHERE u.id = :userId AND wf.status != 'DELETED' AND wf.status <> 'PRE_REGISTERED'
    """)
    Page<WorkflowListResponse> findWorkflows(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT wf
            FROM Workflow wf
            JOIN FETCH wf.user u
            JOIN FETCH wf.trendCategory tc
            LEFT JOIN FETCH wf.recurrenceRule rr
            JOIN FETCH wf.userBlog ub
            LEFT JOIN FETCH ub.blogType bt
            WHERE u.id = :userId AND wf.id = :workflowId AND wf.status <> 'PRE_REGISTERED'
    """)
    Optional<Workflow> findWorkflow(@Param("userId") Long userId, @Param("workflowId") Long workflowId);

    /**
     * SpringBoot가 실행될 때 활성화된 Workflow를 Quartz 스케줄링하기 위해서 사용되는 메서드
     * */
    @Query("""
        SELECT wf
        FROM Workflow wf
        LEFT JOIN FETCH wf.recurrenceRule rr
        WHERE wf.status = 'ACTIVE'
    """)
    List<Workflow> findAllActive();

    @Query("""
            SELECT wf
            FROM Workflow wf
            JOIN FETCH wf.user u
            JOIN FETCH wf.trendCategory tc
            LEFT JOIN FETCH wf.recurrenceRule rr
            JOIN FETCH wf.userBlog ub
            LEFT JOIN FETCH ub.blogType bt
    """)
    Page<Workflow> findWorkflowsForAdmin(Pageable pageable);

    @Query("""
            SELECT wf
            FROM Workflow wf
            JOIN FETCH wf.user u
            JOIN FETCH wf.trendCategory tc
            LEFT JOIN FETCH wf.recurrenceRule rr
            JOIN FETCH wf.userBlog ub
            LEFT JOIN FETCH ub.blogType bt
            WHERE u.id = :userId
    """)
    Page<Workflow> findWorkflowsForAdminByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
           
    @Query("""
        select w
        from Workflow w
        left join fetch w.recurrenceRule rr
        where w.status = 'PENDING'
    """)
    List<Workflow> findAllPending();

    @Query("""
        select w
        from Workflow w
        join fetch w.recurrenceRule
        where w.id = :id
    """)
    Optional<Workflow> findByIdWithRecurrenceRule(@Param("id") Long id);

    @Query("""
        SELECT wf
        FROM Workflow wf
        WHERE wf.status = 'PRE_REGISTERED' AND wf.createdAt < :threshold
    """)
    List<Workflow> findStaleTestWorkflows(@Param("threshold") LocalDateTime threshold);

}
