package com.ocp.ocp_finalproject.monitoring.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 시스템 일별 통계 엔티티
 * 매일 집계되는 시스템 사용 통계 정보를 저장
 */
@Entity
@Table(name = "system_daily_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemDailyStatistics extends BaseEntity {

    /**
     * 통계 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id", nullable = false)
    private Long id;

    /**
     * 통계 날짜
     */
    @Column(name = "stat_date")
    private LocalDate statDate;

    /**
     * 총 사용자 수
     */
    @Column(name = "total_users")
    private Integer totalUsers;

    /**
     * 사용자 증가율
     */
    @Column(name = "user_growth_rate", precision = 5, scale = 2)
    private BigDecimal userGrowthRate;

    /**
     * 총 워크플로우 수
     */
    @Column(name = "total_workflows")
    private Integer totalWorkflows;

    /**
     * 워크플로우 증가율
     */
    @Column(name = "workflow_growth_rate")
    private Integer workflowGrowthRate;

    /**
     * 당일 발행된 포스팅 수
     */
    @Column(name = "posts_today")
    private Integer postsToday;

    /**
     * 게시글 증가율
     */
    @Column(name = "post_growth_rate", precision = 5, scale = 2)
    private BigDecimal postGrowthRate;

    /**
     * 총 AI 요청 수
     */
    @Column(name = "total_ai_requests")
    private Integer totalAiRequests;

    /**
     * 총 AI 비용
     */
    @Column(name = "total_ai_cost", precision = 10, scale = 2)
    private BigDecimal totalAiCost;

    /**
     * AI 비용 증가율
     */
    @Column(name = "ai_cost_growth_rate", precision = 5, scale = 2)
    private BigDecimal aiCostGrowthRate;

    /**
     * 오늘 활성 사용자
     */
    @Column(name = "active_users_today")
    private Integer activeUsersToday;

    /**
     * 활성 사용자 증가율
     */
    @Column(name = "active_user_growth_rate", precision = 5, scale = 2)
    private BigDecimal activeUserGrowthRate;

    @Builder(builderMethodName = "createBuilder")
    public static SystemDailyStatistics create(LocalDate statDate, Integer totalUsers, BigDecimal userGrowthRate,
                                              Integer totalWorkflows, Integer workflowGrowthRate, Integer postsToday,
                                              BigDecimal postGrowthRate, Integer totalAiRequests, BigDecimal totalAiCost,
                                              BigDecimal aiCostGrowthRate, Integer activeUsersToday, BigDecimal activeUserGrowthRate) {
        SystemDailyStatistics systemDailyStatistics = new SystemDailyStatistics();
        systemDailyStatistics.statDate = statDate;
        systemDailyStatistics.totalUsers = totalUsers;
        systemDailyStatistics.userGrowthRate = userGrowthRate;
        systemDailyStatistics.totalWorkflows = totalWorkflows;
        systemDailyStatistics.workflowGrowthRate = workflowGrowthRate;
        systemDailyStatistics.postsToday = postsToday;
        systemDailyStatistics.postGrowthRate = postGrowthRate;
        systemDailyStatistics.totalAiRequests = totalAiRequests;
        systemDailyStatistics.totalAiCost = totalAiCost;
        systemDailyStatistics.aiCostGrowthRate = aiCostGrowthRate;
        systemDailyStatistics.activeUsersToday = activeUsersToday;
        systemDailyStatistics.activeUserGrowthRate = activeUserGrowthRate;
        return systemDailyStatistics;
    }
}