package com.ocp.ocp_finalproject.monitoring.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 통계 엔티티
 * 워크플로우별 일별 집계 통계
 */
@Entity
@Table(name = "daily_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStatistics extends BaseEntity {

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
     * 총 게시글 수
     */
    @Column(name = "total_posts")
    private Integer totalPosts;

    /**
     * 성공한 게시글 수
     */
    @Column(name = "successful_posts")
    private Integer successfulPosts;

    /**
     * 게시글 증가율
     */
    @Column(name = "post_growth_rate", precision = 5, scale = 2)
    private BigDecimal postGrowthRate;

    /**
     * AI 요청 수
     */
    @Column(name = "ai_requests")
    private Integer aiRequests;

    /**
     * AI 비용
     */
    @Column(name = "ai_cost", precision = 10, scale = 2)
    private BigDecimal aiCost;

    /**
     * AI 증가율
     */
    @Column(name = "ai_growth_rate", precision = 5, scale = 2)
    private BigDecimal aiGrowthRate;

    /**
     * 오늘 값
     */
    @Column(name = "today_value")
    private Integer todayValue;

    /**
     * 오늘 증가율
     */
    @Column(name = "today_growth_rate", precision = 5, scale = 2)
    private BigDecimal todayGrowthRate;

    /**
     * 워크플로우 ID (Foreign Key)
     */
    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Builder(builderMethodName = "createBuilder")
    public static DailyStatistics create(LocalDate statDate, Integer totalUsers, BigDecimal userGrowthRate,
                                        Integer totalPosts, Integer successfulPosts, BigDecimal postGrowthRate,
                                        Integer aiRequests, BigDecimal aiCost, BigDecimal aiGrowthRate,
                                        Integer todayValue, BigDecimal todayGrowthRate, Long workflowId) {
        DailyStatistics dailyStatistics = new DailyStatistics();
        dailyStatistics.statDate = statDate;
        dailyStatistics.totalUsers = totalUsers;
        dailyStatistics.userGrowthRate = userGrowthRate;
        dailyStatistics.totalPosts = totalPosts;
        dailyStatistics.successfulPosts = successfulPosts;
        dailyStatistics.postGrowthRate = postGrowthRate;
        dailyStatistics.aiRequests = aiRequests;
        dailyStatistics.aiCost = aiCost;
        dailyStatistics.aiGrowthRate = aiGrowthRate;
        dailyStatistics.todayValue = todayValue;
        dailyStatistics.todayGrowthRate = todayGrowthRate;
        dailyStatistics.workflowId = workflowId;
        return dailyStatistics;
    }
}