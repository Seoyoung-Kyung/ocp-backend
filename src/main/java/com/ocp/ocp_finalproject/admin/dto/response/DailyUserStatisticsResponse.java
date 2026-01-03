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
public class DailyUserStatisticsResponse {

    private LocalDate statDate;
    private Integer totalUsers;
    private BigDecimal userGrowthRate;
    private Integer activeUsersToday;
    private BigDecimal activeUserGrowthRate;

    public static DailyUserStatisticsResponse from(SystemDailyStatistics statistics){
        return DailyUserStatisticsResponse.builder()
                .statDate(statistics.getStatDate())
                .totalUsers(statistics.getTotalUsers())
                .userGrowthRate(statistics.getUserGrowthRate())
                .activeUsersToday(statistics.getActiveUsersToday())
                .activeUserGrowthRate(statistics.getActiveUserGrowthRate())
                .build();
    }

}
