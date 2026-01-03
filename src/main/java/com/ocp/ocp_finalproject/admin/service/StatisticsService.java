package com.ocp.ocp_finalproject.admin.service;

//user
import com.ocp.ocp_finalproject.admin.dto.response.DailyUserStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.MonthlyUserStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.WeeklyUserStatisticsResponse;
//post
import com.ocp.ocp_finalproject.admin.dto.response.DailyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.WeeklyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.MonthlyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.BlogPlatformStatisticsResponse;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.monitoring.domain.SystemDailyStatistics;
import com.ocp.ocp_finalproject.monitoring.repository.SystemDailyStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
/*
* 관리자 통계 서비스
*
* 관리자가 조회하는 각종 통계 데이터를 제공하는 비즈니스 로직을 담당
* 일별, 주별, 월별 사용자 통계 조회 및 집계 기능을 제공
*
* 모든 조회 메서드는 읽기 전용 트랜잭션으로 실행
* */
public class StatisticsService {

    private final SystemDailyStatisticsRepository systemDailyStatisticsRepository;
    private final AiContentRepository aiContentRepository;
    /*
    * 일별 사용자 통계 조회
    *
    * 지정된 기간의 일별 사용자 통계를 조회하여 반환
    * 각 날짜별로 총 사용자 수, 증가율, 활성 사용자 정보를 포함
    *
    * 데이터는 날짜 오름차순으로 정렬되어 반환
    *
    * @param startDate 조회 시작 날짜 (포함)
    * @param endDate 조회 종료 날짜 (포함)
    * @return 일별 사용자 통계 응답 리스트
    * */
    public List<DailyUserStatisticsResponse> getDailyUserStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("일별 사용자 통계 조회 - startDate: {}, endDate: {}", startDate, endDate);

        // 날짜 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

        // Repository를 통해 기간 내 통계 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("조회된 일별 통계 건수: {}", statistics.size());

        // 엔티티를 DTO로 변환하여 반환
        return statistics.stream()
                .map(DailyUserStatisticsResponse::from)
                .collect(Collectors.toList());
    }

    /*
     * 주별 사용자 통계 조회
     *
     * 지정된 년월의 일별 데이터를 주 단위로 집계하여 반환
     *
     * <집계 방식>
     * 주차 계산: 월요일 시작
     * 총 사용자: 해당 주 마지막 날의 값 사용
     * 증가율: 해당 주 마지막 날의 값 사용
     * 활성 사용자: 해당 주의 일별 평균값 사용
     *
     * @param year 조회할 년도 (예: 2025)
     * @param month 조회할 월 (1-12)
     * @return 주별 사용자 통계 응답 리스트 (주차 오름차순)
     * */
    public List<WeeklyUserStatisticsResponse> getWeeklyUserStatistics(int year, int month) {
        log.info("주별 사용자 통계 - year: {}, month: {}", year, month);

        // 월 유효성 검사
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("월(month)은 1에서 12 사이의 값이어야 합니다.");
        }

        // 날짜 범위 계산 (성능 최적화: 인덱스 활용 가능)
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 해당 년월의 모든 일별 통계 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("주별 집계를 위한 일별 데이터 조회 건수: {}", statistics.size());

        // 주차별로 그룹화
        // 월요일 시작, 첫 주는 목요일을 포함한 주
        Map<Integer, List<SystemDailyStatistics>> weeklyGroup = statistics.stream()
                .collect(Collectors.groupingBy(stat ->
                        stat.getStatDate().get(WeekFields.ISO.weekOfMonth())
                ));

        log.info("집계된 주차 수: {}", weeklyGroup.size());

        // 각 주차별 데이터를 응답 DTO로 변환
        return weeklyGroup.entrySet().stream()
                .map(entry -> {
                    Integer weekNumber = entry.getKey();
                    List<SystemDailyStatistics> weekData = entry.getValue();

                    // 주의 마지막 날 데이터 (총 사용자, 증가율 계산에 사용)
                    SystemDailyStatistics lastDayStat = weekData.get(weekData.size() - 1);

                    // 주의 시작일과 종료일 계산
                    LocalDate weekStart = weekData.get(0).getStatDate();
                    LocalDate weekEnd = lastDayStat.getStatDate();

                    // 주간 평균 활성 사용자 계산
                    // 각 날짜의 활성 사용자 수를 평균
                    double avgActiveUsers = weekData.stream()
                            .mapToInt(SystemDailyStatistics::getActiveUsersToday)
                            .average()
                            .orElse(0.0);

                    log.debug("{}주차 집계 - 기간: {} ~ {}, 평균 활성 사용자: {}",
                            weekNumber, weekStart, weekEnd, avgActiveUsers);

                    return WeeklyUserStatisticsResponse.builder()
                            .weekNumber(weekNumber)
                            .weekPeriod(weekStart + " ~ " + weekEnd)
                            .totalUsers(lastDayStat.getTotalUsers())
                            .userGrowthRate(lastDayStat.getUserGrowthRate())
                            .activeUsers((long) avgActiveUsers)
                            .activeUserGrowthRate(lastDayStat.getActiveUserGrowthRate())
                            .build();
                })
                .sorted(Comparator.comparing(WeeklyUserStatisticsResponse::getWeekNumber))
                .collect(Collectors.toList());
    }

    /*
    * 월별 사용자 통계를 조회
    *
    * 지정된 년도의 월별 사용자 통계를 조회
    * 일별 데이터를 월 단위로 집계하여 1월부터 12월까지 반환
    *
    * <집계 방식>
    * 총 사용자: 해당 월 마지막 날의 값 사용
    * 증가율: 해당 월 마지막 날의 값 사용
    * 활성 사용자: 해당 월의 일별 평균값 사용
    *
    * @param year 조회할 년도 (예: 2025)
    * @return 월별 사용자 통계 리스트 (월 오름차순, 1월~12월)
    * */
    public List<MonthlyUserStatisticsResponse> getMonthlyUserStatistics(int year) {
        log.info("월별 사용자 통계 조회 - year: {}", year);

        // 날짜 범위 계산 (성능 최적화: 인덱스 활용 가능)
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // 해당 년도의 모든 일별 통계 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("월별 집계를 위한 일별 데이터 조회 건수: {}", statistics.size());

        //월별로 그룹화 (1~12)
        Map<Integer, List<SystemDailyStatistics>> monthlyGroup = statistics.stream()
                .collect(Collectors.groupingBy(stat -> stat.getStatDate().getMonthValue()));

        log.info("집계된 월 수: {}", monthlyGroup.size());

        // 각 월별 데이터를 응답 DTO로 변환
        return monthlyGroup.entrySet().stream()
                .map(entry -> {
                    Integer month = entry.getKey();
                    List<SystemDailyStatistics> monthData = entry.getValue();

                    // 월의 마지막 날 데이터 (총 사용자, 증가율 계산에 사용)
                    SystemDailyStatistics lastDayStat = monthData.get(monthData.size() - 1);

                    // 월간 평균 활성 사용자 계산
                    // 각 날짜의 활성 사용자 수를 평균
                    double avgActiveUsers = monthData.stream()
                            .mapToInt(SystemDailyStatistics::getActiveUsersToday)
                            .average()
                            .orElse(0.0);

                    log.debug("{}월 집계 - 일수: {}, 평균 활성 사용자: {}",
                            month, monthData.size(), avgActiveUsers);

                    return MonthlyUserStatisticsResponse.builder()
                            .month(month)
                            .monthName(String.format("%d-%02d", year, month))
                            .totalUsers(lastDayStat.getTotalUsers())
                            .userGrowthRate(lastDayStat.getUserGrowthRate())
                            .activeUsers((long) avgActiveUsers)
                            .activeUserGrowthRate(lastDayStat.getActiveUserGrowthRate())
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyUserStatisticsResponse::getMonth))
                .collect(Collectors.toList());
    }
    
    // 포스팅 통계 조회

    /*
    * 일별 포스팅 조회
    *
    * 지정된 기간의 일별 발행된 포스팅 수를 조회
    *
    * @param startDate 조회 시작 날짜 (포함)
    * @param endDate 조회 종료 날짜 (포함)
    * @return 일별 포스팅 통계 응답 리스트
    * */
    public List<DailyPostStatisticsResponse> getDailyPostStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("일별 포스팅 통계 조회 - startDate: {}, endDate: {}", startDate, endDate);

        // 날짜 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

        // SystemDailyStatistics에서 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("조회된 일별 포스팅 통계 건수: {}", statistics.size());

        return statistics.stream()
                .map(stat -> DailyPostStatisticsResponse.builder()
                        .statDate(stat.getStatDate())
                        .postCount(stat.getPostsToday() != null ? stat.getPostsToday().longValue() : 0L)
                        .build())
                .collect(Collectors.toList());
    }

    /*
    * 주별 포스팅 통계 조회
    *
    * 지정된 년월의 일별 데이터를 주 단위로 집계하여 반환
    * ISO-8601 기준 주차 계산
    *
    * @param year 조회할 년도
    * @param month 조회할 월
    * @return 주별 포스팅 통계 응답 리스트
    * */
    public List<WeeklyPostStatisticsResponse> getWeeklyPostStatistics(int year, int month) {
        log.info("주별 포스팅 통계 조회 - year: {}, month: {}", year, month);

        // 월 유효성 검사
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("월(month)은 1에서 12 사이의 값이어야 합니다.");
        }

        // 날짜 범위 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // SystemDailyStatistics에서 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("주별 포스팅 집계를 위한 일별 데이터 조회 건수: {}", statistics.size());

        // 주차별로 그룹화 (연도 경계 처리 포함)
        Map<String, List<SystemDailyStatistics>> weeklyGroup = statistics.stream()
                .collect(Collectors.groupingBy(stat -> {
                    int weekBasedYear = stat.getStatDate().get(WeekFields.ISO.weekBasedYear());
                    int weekOfYear = stat.getStatDate().get(WeekFields.ISO.weekOfWeekBasedYear());
                    return String.format("%d-W%02d", weekBasedYear, weekOfYear);
                }));

        log.info("집계된 포스팅 주차 수: {}", weeklyGroup.size());

        return weeklyGroup.entrySet().stream()
                .map(entry -> {
                    String weekString = entry.getKey();
                    List<SystemDailyStatistics> weekData = entry.getValue();

                    // 주의 시작일과 종료일
                    LocalDate weekStart = weekData.get(0).getStatDate();
                    LocalDate weekEnd = weekData.get(weekData.size() - 1).getStatDate();

                    // 주간 총 포스팅 수 (합계)
                    long totalPosts = weekData.stream()
                            .mapToLong(stat -> stat.getPostsToday() != null ? stat.getPostsToday().longValue() : 0L)
                            .sum();

                    log.debug("{} 주차 집계 - 기간: {} ~ {}, 총 포스팅: {}",
                            weekString, weekStart, weekEnd, totalPosts);

                    return WeeklyPostStatisticsResponse.builder()
                            .week(weekString)
                            .startDate(weekStart.toString())
                            .endDate(weekEnd.toString())
                            .postCount(totalPosts)
                            .build();
                })
                .sorted(Comparator.comparing(WeeklyPostStatisticsResponse::getWeek))
                .collect(Collectors.toList());
    }

    /*
    * 월별 포스팅 통계 조회
    *
    * 지정된 년도의 일별 데이터를 월 단위로 집계하여 반환
    *
    * @param year 조회할 년도
    * @return 월별 포스팅 통계 응답 리스트
    * */
    public List<MonthlyPostStatisticsResponse> getMonthlyPostStatistics(int year) {
        log.info("월별 포스팅 통계 조회 - year: {}", year);

        // 날짜 범위 계산
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // SystemDailyStatistics에서 조회
        List<SystemDailyStatistics> statistics = systemDailyStatisticsRepository
                .findByStatDateBetweenOrderByStatDateAsc(startDate, endDate);

        log.info("월별 집계를 위한 일별 데이터 조회 건수: {}", statistics.size());

        // 월별로 그룹화
        Map<Integer, List<SystemDailyStatistics>> monthlyGroup = statistics.stream()
                .collect(Collectors.groupingBy(stat -> stat.getStatDate().getMonthValue()));

        log.info("집계된 포스팅 월 수: {}", monthlyGroup.size());

        return monthlyGroup.entrySet().stream()
                .map(entry -> {
                    Integer monthNumber = entry.getKey();
                    List<SystemDailyStatistics> monthData = entry.getValue();

                    // 월의 시작일과 종료일 (해당 월의 실제 첫날과 마지막 날)
                    LocalDate monthStart = LocalDate.of(year, monthNumber, 1);
                    LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

                    // 월간 총 포스팅 수 (합계)
                    long totalPosts = monthData.stream()
                            .mapToLong(stat -> stat.getPostsToday() != null ? stat.getPostsToday().longValue() : 0L)
                            .sum();

                    log.debug("{}월 집계 - 일수: {}, 총 포스팅: {}",
                            monthNumber, monthData.size(), totalPosts);

                    return MonthlyPostStatisticsResponse.builder()
                            .yearMonth(String.format("%d-%02d", year, monthNumber))
                            .startDate(monthStart.toString())
                            .endDate(monthEnd.toString())
                            .postCount(totalPosts)
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyPostStatisticsResponse::getYearMonth))
                .collect(Collectors.toList());
    }

    /*
    * 플랫폼별 발행 통계 조회
    *
    * 지정된 기간의 블로그 플랫폼별 발행된 포스팅 수를 조회합니다.
    *
    * @param startDate 조회 시작 날짜 (포함)
    * @param endDate 조회 종료 날짜 (포함)
    * @return 플랫폼별 포스팅 통계 리스트
    * */
    public List<BlogPlatformStatisticsResponse> getBlogPlatformStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("플랫폼별 발행 통계 조회 - startDate: {}, endDate: {}", startDate, endDate);

        // 날짜 유효성 검사
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

        // 시작일 00:00:00, 종료일 23:59:59
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // AiContentRepository에서 플랫폼별 집계 조회
        List<Object[]> results = aiContentRepository.countByBlogPlatform(startDateTime, endDateTime);

        log.info("조회된 플랫폼 수: {}", results.size());

        return results.stream()
                .map(BlogPlatformStatisticsResponse::from)
                .collect(Collectors.toList());
    }
}
