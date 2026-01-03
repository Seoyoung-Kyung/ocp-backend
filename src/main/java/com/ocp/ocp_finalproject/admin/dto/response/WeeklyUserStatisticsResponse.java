package com.ocp.ocp_finalproject.admin.dto.response;

import com.ocp.ocp_finalproject.monitoring.domain.SystemDailyStatistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyUserStatisticsResponse {

    private Integer weekNumber;
    private String weekPeriod; // "2025-11-01 ~ 2025-11-07"
    private Integer totalUsers;
    private BigDecimal userGrowthRate;
    private Long activeUsers;
    private BigDecimal activeUserGrowthRate;

}
