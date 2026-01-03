package com.ocp.ocp_finalproject.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostStatisticsResponse {

    /*
    * 통계 날짜
    * */
    private LocalDate statDate;

    /*
    * 해당 날짜에 발행된 포스팅 수
    * */
    private Long postCount;

    /*
    * Object[] 쿼리 결과를 DTO로 변환
    *
    * @param result [LocalDate statDate, Long postCount]
    * @return DailyPostStatisticsResponse
    * */
    public static DailyPostStatisticsResponse from(Object[] result){
        return DailyPostStatisticsResponse.builder()
                .statDate((LocalDate) result[0])
                .postCount(((Number) result[1]).longValue())
                .build();
    }
}
