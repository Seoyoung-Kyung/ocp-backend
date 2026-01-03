package com.ocp.ocp_finalproject.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyUserStatisticsResponse {

    private Integer month;
    private String monthName; // "2025-11"
    private Integer totalUsers;
    private BigDecimal userGrowthRate;
    private Long activeUsers;
    private BigDecimal activeUserGrowthRate;

}
