package com.ocp.ocp_finalproject.monitoring.service;

import com.ocp.ocp_finalproject.content.enums.ContentStatus;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.monitoring.domain.SystemDailyStatistics;
import com.ocp.ocp_finalproject.monitoring.repository.SystemDailyStatisticsRepository;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/*
 * TODO: 향후 리팩토링 필요
 * 1. 계산 로직 분리 (StatisticsCalculator 유틸리티 클래스 생성)
 * 2. 데이터 조회 로직 분리 (StatisticsDataProvider 생성)
 * 3. 긴 메서드 분할 (aggregateAndSaveDailyStatistics 메서드 간소화)
 * 4. Repository 직접 주입 대신 Service 레이어 활용
 * 5. Magic Number 상수화 (divide의 4 등)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsAggregationService {

    private final SystemDailyStatisticsRepository systemDailyStatisticsRepository;
    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final AiContentRepository aiContentRepository;

    @Transactional
    public void aggregateAndSaveDailyStatistics(LocalDate targetDate){
        log.info("통계 집계 시작 - 날짜: {}", targetDate);

        // 0. 중복 체크 - 이미 해당 날짜 통계가 있으면 스킵
        if (systemDailyStatisticsRepository.existsByStatDate(targetDate)) {
            log.warn("통계 집계 스킵 - 이미 존재하는 날짜: {}", targetDate);
            return;
        }

        // 1. 총 사용자 수 (누적)
        Integer totalUsers = Math.toIntExact(userRepository.count());

        // 2. 전날 총 사용자 수 가져오기
        LocalDate previousDate = targetDate.minusDays(1);
        Optional<SystemDailyStatistics> previousStats =
                systemDailyStatisticsRepository.findByStatDate(previousDate);

        Integer previousTotalUsers = previousStats
                .map(SystemDailyStatistics::getTotalUsers)
                .orElse(0);

        // 3. 사용자 증가율 계산
        BigDecimal userGrowthRate = calculateGrowthRate(totalUsers, previousTotalUsers);

        // 4. 당일 활성 사용자 수 (예: 당일 생성된 사용자 or 로그인한 사용자)
        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = targetDate.plusDays(1).atStartOfDay();
        Integer activeUsersToday = countActiveUsers(dayStart, dayEnd);

        // 5. 사용자 증가율 계산
        Integer previousActiveUsers = previousStats
                .map(SystemDailyStatistics::getActiveUsersToday)
                .orElse(0);

        // 6. 활성 사용자 증가율
        BigDecimal activeUserGrowthRate = calculateGrowthRate(activeUsersToday, previousActiveUsers);

        // 7. 총 워크플로우 수
        Integer totalWorkflows = Math.toIntExact(workflowRepository.count());

        // 8. 워크플로우 증가율
        Integer previousWorkflows = previousStats
                .map(SystemDailyStatistics::getTotalWorkflows)
                .orElse(0);

        Integer workflowGrowthRate = totalWorkflows - previousWorkflows;

        // 9. 당일 발행된 포스팅 수 (PUBLISHED 상태)
        Integer postsToday = Math.toIntExact(
                aiContentRepository.countByStatusAndCompletedAtBetween(
                        ContentStatus.PUBLISHED, dayStart, dayEnd));

        // 10. 포스팅 증가율
        Integer previousPostsToday = previousStats
                .map(SystemDailyStatistics::getPostsToday)
                .orElse(0);

        BigDecimal postGrowthRate = calculateGrowthRate(postsToday, previousPostsToday);

        // 11. SystemDailyStatistics 엔티티 생성 및 저장
        SystemDailyStatistics statistics = SystemDailyStatistics.createBuilder()
                .statDate(targetDate)
                .totalUsers(totalUsers)
                .userGrowthRate(userGrowthRate)
                .totalWorkflows(totalWorkflows)
                .workflowGrowthRate(workflowGrowthRate)
                .postsToday(postsToday)
                .postGrowthRate(postGrowthRate)
                .totalAiRequests(0)  // 필요시 계산
                .totalAiCost(BigDecimal.ZERO)  // 필요시 계산
                .aiCostGrowthRate(BigDecimal.ZERO)  // 필요시 계산
                .activeUsersToday(activeUsersToday)
                .activeUserGrowthRate(activeUserGrowthRate)
                .build();

        systemDailyStatisticsRepository.save(statistics);

        log.info("통계 저장 완료 - 날짜: {}, 총 사용자: {}, 사용자 증가율: {}%, 활성 사용자: {}, 당일 포스팅: {}, 포스팅 증가율: {}%",
                targetDate, totalUsers, userGrowthRate, activeUsersToday, postsToday, postGrowthRate);
    }


    /*
    * 증가율 계산 (%)
    * (현재값 - 이전값) / 이전값 * 100
    * */
    private BigDecimal calculateGrowthRate(Integer current, Integer previous){
        if (previous == 0){
            return current == 0 ? BigDecimal.ZERO : new BigDecimal("100.00");
        }

        BigDecimal currentValue = new BigDecimal(current);
        BigDecimal previousValue = new BigDecimal(previous);

        return currentValue
                .subtract(previousValue)
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }


    /*
    * 활성 사용자 수 계산
    * 현재: updatedAt 기준으로 활성 사용자 추정
    * TODO: 나중에 로그인 로그 테이블이 생기면 더 정확하게 측정 가능
    * */
    private Integer countActiveUsers(LocalDateTime startTime, LocalDateTime endTime){
        Long count = userRepository.countActiveUsersBetween(startTime, endTime);
        return Math.toIntExact(count);
    }

    /*
    * 특정 날짜의 통계를 재집계합니다.
    *
    * 기존 통계 데이터가 있으면 삭제한 후 새로 집계합니다.
    * 수동으로 통계를 재집계할 때 사용합니다.
    *
    * @param targetDate 재집계할 날짜
    * */
    @Transactional
    public void reAggregateStatistics(LocalDate targetDate) {
        log.info("통계 재집계 시작 - 날짜: {}", targetDate);

        // 1. 기존 통계 데이터 삭제
        systemDailyStatisticsRepository.deleteByStatDate(targetDate);
        log.info("기존 통계 데이터 삭제 완료 - 날짜: {}", targetDate);

        // 2. 새로 집계
        aggregateAndSaveDailyStatistics(targetDate);
        log.info("통계 재집계 완료 - 날짜: {}", targetDate);
    }
}
