package com.ocp.ocp_finalproject.work.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private WorkExecutionStatus status;

    @Column(name="posting_url")
    private String postingUrl;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "view_count")
    private Integer viewCount;

    @OneToOne(mappedBy = "work", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private AiContent aiContent;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Builder(builderMethodName = "createBuilder")
    public static Work create(Workflow workflow, WorkExecutionStatus status, LocalDateTime startedAt, LocalDateTime completedAt) {
        Work work = new Work();
        work.workflow = workflow;
        work.status = status;
        work.startedAt = startedAt;
        work.completedAt = completedAt;
        work.viewCount = 0;
        return work;
    }

    public void updateUrlCompletion(String postingUrl, boolean isSuccess, LocalDateTime completedAt, String failureReason) {
        this.postingUrl = postingUrl;
        this.completedAt = completedAt;
        if (isSuccess) {
            this.status = WorkExecutionStatus.COMPLETED;
            this.failureReason = null;
        } else {
            this.status = WorkExecutionStatus.FAILED;
            this.failureReason = failureReason;
        }
    }

    public void updateKeywordCompletion(boolean isSuccess, LocalDateTime startedAt, LocalDateTime completedAt, String failureReason) {
        if (isSuccess) {
            this.status = WorkExecutionStatus.TREND_KEYWORD_DONE;
            this.failureReason = null;
        } else {
            this.status = WorkExecutionStatus.FAILED;
            this.failureReason = failureReason;
        }

        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public void markRequested() {
        this.status = WorkExecutionStatus.REQUESTED;
    }

    public void updateProductSelection(boolean isSuccess, LocalDateTime completedAt, String failureReason) {
        if (isSuccess) {
            this.status = WorkExecutionStatus.PRODUCT_SELECTED;
            this.failureReason = null;
        } else {
            this.status = WorkExecutionStatus.FAILED;
            this.failureReason = failureReason;
        }
        this.completedAt = completedAt;
    }

    public void updateContentGeneration(boolean isSuccess, LocalDateTime completedAt, String failureReason) {
        if (isSuccess) {
            this.status = WorkExecutionStatus.CONTENT_GENERATED;
            this.failureReason = null;
        } else {
            this.status = WorkExecutionStatus.FAILED;
            this.failureReason = failureReason;
        }
        this.completedAt = completedAt;
    }
}
