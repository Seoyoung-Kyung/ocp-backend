package com.ocp.ocp_finalproject.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyPostStatisticsResponse {

    /*
    * 년월
    * 예: "2025-01"
    * */
    private String yearMonth;

    /*
    * 월의 시작 날짜
    * 예: "2025-01-01"
    * */
    private String startDate;

    /*
    * 월의 종료 날짜
    * 예: "2025-01-31"
    * */
    private String endDate;

    /*
    * 해당 월에 발행된 총 포스팅 수
    * */
    private Long postCount;
}
