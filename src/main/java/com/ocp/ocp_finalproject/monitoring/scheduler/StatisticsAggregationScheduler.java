package com.ocp.ocp_finalproject.monitoring.scheduler;

import com.ocp.ocp_finalproject.monitoring.service.StatisticsAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsAggregationScheduler {

    private final StatisticsAggregationService statisticsAggregationService;

    /*
    * 매일 자정 1분에 전날 통계 집계
    * cron: 초 분 시 일 월 요일
    * */
    @Scheduled(cron = "0 1 0 * * *")
    public void aggregateDailyStatistics(){
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("===== 일별 통계 집계 시작: {} ====", yesterday);

        try{
            statisticsAggregationService.aggregateAndSaveDailyStatistics(yesterday);
            log.info("==== 일별 통계 집계 완료: {} ====", yesterday);
        }catch (Exception e){
            log.error("일별 통계 집계 실패: {}", yesterday, e);
        }
    }

}
