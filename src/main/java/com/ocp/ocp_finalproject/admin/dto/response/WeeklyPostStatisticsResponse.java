package com.ocp.ocp_finalproject.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 주별 포스팅 통계 응답 DTO
* 
* 관리자가 조회하는 주별 포스팅 통계 정보를 담는 응답 객체
* */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyPostStatisticsResponse{

    /*
    * 주차 (ISO-8601 형식)
    * 예: "2025-W01"
    * */
    private String week;

    /*
    * 주의 시작 날짜
    * 예: "2024-12-30"
    * */
    private String startDate;

    /*
    * 주의 종료 날짜
    * 예: "2025-01-05"
    * */
    private String endDate;

    /*
    * 해당 주에 발행된 총 포스팅 수
    * */
    private Long postCount;
}
