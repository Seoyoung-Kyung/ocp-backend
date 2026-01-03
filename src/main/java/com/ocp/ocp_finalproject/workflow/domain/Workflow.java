package com.ocp.ocp_finalproject.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ocp.ocp_finalproject.blog.domain.UserBlog;
import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.INVALID_STATUS_CHANGE;

@Entity
@Table(name = "workflow")
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Workflow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_blog_id")
    private UserBlog userBlog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_category_id")
    private TrendCategory trendCategory;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recurrence_rule_id")
    private RecurrenceRule recurrenceRule;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Work> works = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private WorkflowStatus status;

    @Column(name = "site_url", length = 100)
    private String siteUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_status", length = 50)
    private WorkflowTestStatus testStatus;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public static Workflow create(
            User user,
            UserBlog userBlog,
            TrendCategory trendCategory,
            RecurrenceRule recurrenceRule,
            String siteUrl
    ) {
        Workflow workflow = new Workflow();
        workflow.user = user;
        workflow.userBlog = userBlog;
        workflow.trendCategory = trendCategory;
        workflow.recurrenceRule = recurrenceRule;
        workflow.status = WorkflowStatus.PRE_REGISTERED;
        workflow.siteUrl = siteUrl;
        workflow.testStatus = WorkflowTestStatus.NOT_TESTED;

        return workflow;
    }

    public void update(UserBlog userBlog,
                       TrendCategory trendCategory,
                       RecurrenceRule recurrenceRule,
                       String siteUrl) {
        this.userBlog = userBlog;
        this.trendCategory = trendCategory;
        this.recurrenceRule = recurrenceRule;
        this.siteUrl = siteUrl;
    }

    public void changeStatus(WorkflowStatus newStatus) {
        if(!this.status.canTransitionTo(newStatus)) {
            log.info(String.valueOf(this.status));
            log.info(String.valueOf(newStatus));
            throw new CustomException(INVALID_STATUS_CHANGE);
        }

        this.status = newStatus;
    }

    public void delete() {
        this.status = WorkflowStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void markAsTest() {
        this.testStatus = WorkflowTestStatus.TESTING;
    }

    public void updateTestStatus(WorkflowTestStatus newStatus) {
        this.testStatus = newStatus;
    }
}