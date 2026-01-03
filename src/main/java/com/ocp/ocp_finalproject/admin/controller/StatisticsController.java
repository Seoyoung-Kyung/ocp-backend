package com.ocp.ocp_finalproject.admin.controller;

//user
import com.ocp.ocp_finalproject.admin.dto.response.DailyUserStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.MonthlyUserStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.WeeklyUserStatisticsResponse;
//post
import com.ocp.ocp_finalproject.admin.dto.response.DailyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.WeeklyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.MonthlyPostStatisticsResponse;
import com.ocp.ocp_finalproject.admin.dto.response.BlogPlatformStatisticsResponse;
import com.ocp.ocp_finalproject.admin.service.StatisticsService;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.monitoring.service.StatisticsAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/*
* 관리자 통계 컨트롤러
* 
* 관리자가 시스템 사용 통계를 조회하는 API를 제공
* 일별, 주별, 월별 사용자 통계 조회 엔트포인트를 포함합니다.
*
* <주요 기능>
* 일별, 주별, 월별 통계 조회
*
* <공통 응답 형식>
* 성공: ApiResult.success(message, data)
* 실패: ApiResult.error(errorCode, message)
*
* */
@Tag(name = "관리자 - 통계", description = "관리자 통계 조회 및 관리 API")
@RestController
@RequestMapping("/api/v1/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsAggregationService statisticsAggregationService;
    
    /*
    * 일별 사용자 통계를 조회합니다.
    *
    * 지정된 기간의 일별 사용자 통계(가입자 수, 활성 사용자, 증가율 등)를 조회합니다.
    * 날짜는 (YYYY-MM-DD)형식으로 전달
    *
    * <조회 데이터>
    * 날짜별 총 사용자 수 (누적)
    * 사용자 증가율 (전일 대비 %)
    * 당일 활성 사용자 수
    * 활성 사용자 증가율 (전일 대비 %)
    *
    * */
    @Operation(summary = "일별 사용자 통계 조회",
    description = "지정한 기간의 일별 사용자 통계를 조회합니다. 날짜는 (YYYY-MM-DD)형식으로 전달")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일별 사용자 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (날짜 형식 오류, 시작일 > 종료일 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/daily/users")
    public ResponseEntity<ApiResult<List<DailyUserStatisticsResponse>>> getDailyUserStatistics(
            @Parameter(
                    description = "조회 시작 날짜 (ISO-8601 형식)",
                    example = "2025-11-01",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "조회 종료 날짜 (ISO-8601 형식)",
                    example = "2025-11-30",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        List<DailyUserStatisticsResponse> statistics =
                statisticsService.getDailyUserStatistics(startDate, endDate);

        return ResponseEntity.ok(ApiResult.success("일별 사용자 통계 조회 성공", statistics));
    }

    /*
    * 주별 사용자 통계를 조회합니다.
    *
    * 지정된 년월의 주별 사용자 통계를 조회
    * 일별 데이터를 주 단위로 집계하여 반환
    *
    * <집계 방식>
    * 주차 계산 ISO-8601 표준 (월요일 시작)
    * 총 사용자: 해당 주 마지막 날의 값
    * 활성 사용자: 해당 주의 일별 평균값
    *
    * @param year 조회할 년도 (예: 2025)
    * @param month 조회할 월 (1~12)
    * @return 주별 사용자 통계 리스트 (주차 오름차순)
    * */
    @Operation(summary = "주별 사용자 통계 조회",
            description = "지정한 년월의 주별 사용자 통계를 조회합니다. 일별 데이터를 주 단위로 집계"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주별 사용자 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (월이 1-12 범위를 벗어난 경우 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/weekly/users")
    public ResponseEntity<ApiResult<List<WeeklyUserStatisticsResponse>>> getWeeklyUserStatistics(
            @Parameter(
                    description = "조회할 년도",
                    example = "2025",
                    required = true
            )
            @RequestParam int year,

            @Parameter(
                    description = "조회할 월 (1-12)",
                    example = "11",
                    required = true
            )
            @RequestParam int month) {

        List<WeeklyUserStatisticsResponse> statistics =
                statisticsService.getWeeklyUserStatistics(year, month);

        return ResponseEntity.ok(ApiResult.success("주별 사용자 통계 조회 성공", statistics));
    }

    /*
    * 월별 사용자 통계를 조회
    *
    * 지정된 년도의 월별 사용자 통계를 조회
    * 일별 데이터를 월 단위로 집계하여 1월부터 12월까지 반환
    *
    * <집계 방식>
    * 총 사용자: 해당 월 마지막 날의 값
    * 활성 사용자: 해당 월의 일별 평균값
    *
    *   @param year 조회할 년도 (예: 2025)
    * @return 월별 사용자 통계 리스트 (월 오름차순, 1월~12월)
    * */
    @Operation(summary = "월별 사용자 통계 조회",
    description = "지정한 년도의 월별 사용자 통계를 조회합니다. 일별 데이터를 월 단위로 집계합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월별 사용자 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/monthly/users")
    public ResponseEntity<ApiResult<List<MonthlyUserStatisticsResponse>>> getMonthlyUserStatistics(
            @Parameter(
                    description = "조회할 년도",
                    example = "2025",
                    required = true
            )
            @RequestParam int year) {
        List<MonthlyUserStatisticsResponse> statistics =
                statisticsService.getMonthlyUserStatistics(year);

        return ResponseEntity.ok(ApiResult.success("월별 사용자 통계 조회 성공", statistics));
    }
    // ============================================
    // 포스팅 통계 조회
    // ============================================

    /*
     * 일별 포스팅 통계를 조회합니다.
     *
     * 지정된 기간의 일별 발행된 포스팅 수를 조회합니다.
     * 날짜는 (YYYY-MM-DD) 형식으로 전달
     *
     * <조회 데이터>
     * 날짜별 발행된 포스팅 수 (PUBLISHED 상태)
     * AiContent의 completedAt 기준으로 집계
     *
     * */
    @Operation(summary = "일별 포스팅 통계 조회",
            description = "지정한 기간의 일별 발행된 포스팅 수를 조회합니다. 날짜는 (YYYY-MM-DD) 형식으로 전달")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일별 포스팅 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (날짜 형식 오류, 시작일 > 종료일 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/daily/posts")
    public ResponseEntity<ApiResult<List<DailyPostStatisticsResponse>>> getDailyPostStatistics(
            @Parameter(
                    description = "조회 시작 날짜 (ISO-8601 형식)",
                    example = "2025-01-01",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "조회 종료 날짜 (ISO-8601 형식)",
                    example = "2025-01-31",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        List<DailyPostStatisticsResponse> statistics =
                statisticsService.getDailyPostStatistics(startDate, endDate);

        return ResponseEntity.ok(ApiResult.success("일별 포스팅 조회 성공", statistics));
    }

    /*
     * 주별 포스팅 통계를 조회합니다.
     *
     * 지정된 년월의 주별 발행된 포스팅 수를 조회합니다.
     * 일별 데이터를 주 단위로 집계하여 반환
     *
     * <집계 방식>
     * 주차 계산: ISO-8601 표준 (월요일 시작)
     * 총 포스팅: 해당 주의 일별 포스팅 합계
     *
     * @param year 조회할 년도 (예: 2025)
     * @param month 조회할 월 (1-12)
     * @return 주별 포스팅 통계 리스트 (주차 오름차순)
     * */
    @Operation(summary = "주별 포스팅 통계 조회",
            description = "지정한 년월의 주별 발행된 포스팅 수를 조회합니다. 일별 데이터를 주 단위로 집계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주별 포스팅 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (월이 1-12 범위를 벗어난 경우 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/weekly/posts")
    public ResponseEntity<ApiResult<List<WeeklyPostStatisticsResponse>>> getWeeklyPostStatistics(
            @Parameter(
                    description = "조회할 년도",
                    example = "2025",
                    required = true
            )
            @RequestParam int year,

            @Parameter(
                    description = "조회할 월 (1-12)",
                    example = "1",
                    required = true
            )
            @RequestParam int month) {

        List<WeeklyPostStatisticsResponse> statistics =
                statisticsService.getWeeklyPostStatistics(year, month);

        return ResponseEntity.ok(ApiResult.success("주별 포스팅 조회 성공", statistics));
    }

    /*
     * 월별 포스팅 통계를 조회합니다.
     *
     * 지정된 년도의 월별 발행된 포스팅 수를 조회합니다.
     * 일별 데이터를 월 단위로 집계하여 1월부터 12월까지 반환
     *
     * <집계 방식>
     * 총 포스팅: 해당 월의 일별 포스팅 합계
     *
     * @param year 조회할 년도 (예: 2025)
     * @return 월별 포스팅 통계 리스트 (월 오름차순, 1월~12월)
     * */
    @Operation(summary = "월별 포스팅 통계 조회",
            description = "지정한 년도의 월별 발행된 포스팅 수를 조회합니다. 일별 데이터를 월 단위로 집계")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월별 포스팅 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/monthly/posts")
    public ResponseEntity<ApiResult<List<MonthlyPostStatisticsResponse>>> getMonthlyPostStatistics(
            @Parameter(
                    description = "조회할 년도",
                    example = "2025",
                    required = true
            )
            @RequestParam int year) {

        List<MonthlyPostStatisticsResponse> statistics =
                statisticsService.getMonthlyPostStatistics(year);

        return ResponseEntity.ok(ApiResult.success("월별 포스팅 조회 성공", statistics));
    }

    // ============================================
    // 통계 수동 재집계 (관리자용)
    // ============================================

    /*
     * 특정 날짜의 통계를 수동으로 재집계합니다.
     *
     * 기존 통계 데이터가 있으면 삭제 후 재집계합니다.
     * 스케줄러가 실행되지 않았거나 데이터 오류 발생 시 사용합니다.
     *
     * @param targetDate 재집계할 날짜
     * @return 성공 메시지
     * */
    @Operation(summary = "특정 날짜 통계 수동 재집계",
            description = "지정한 날짜의 통계를 수동으로 재집계합니다. 기존 데이터가 있으면 삭제 후 재집계합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 재집계 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/manual-aggregate")
    public ResponseEntity<ApiResult<String>> manualAggregate(
            @Parameter(
                    description = "재집계할 날짜 (ISO-8601 형식)",
                    example = "2025-12-15",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate targetDate) {

        statisticsAggregationService.reAggregateStatistics(targetDate);

        return ResponseEntity.ok(
                ApiResult.success("통계 재집계 완료", targetDate.toString())
        );
    }

    /*
     * 날짜 범위의 통계를 수동으로 일괄 재집계합니다.
     *
     * 시작일부터 종료일까지 각 날짜의 통계를 재집계합니다.
     * 기존 통계 데이터가 있으면 삭제 후 재집계합니다.
     *
     * @param startDate 재집계 시작 날짜
     * @param endDate 재집계 종료 날짜
     * @return 성공 메시지
     * */
    @Operation(summary = "날짜 범위 통계 수동 재집계",
            description = "지정한 날짜 범위의 통계를 일괄 재집계합니다. 기존 데이터가 있으면 삭제 후 재집계합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통계 재집계 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (시작일 > 종료일 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/manual-aggregate-range")
    public ResponseEntity<ApiResult<String>> manualAggregateRange(
            @Parameter(
                    description = "재집계 시작 날짜 (ISO-8601 형식)",
                    example = "2025-12-10",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "재집계 종료 날짜 (ISO-8601 형식)",
                    example = "2025-12-19",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            statisticsAggregationService.reAggregateStatistics(current);
            current = current.plusDays(1);
        }

        return ResponseEntity.ok(
                ApiResult.success("통계 재집계 완료", startDate + " ~ " + endDate)
        );
    }

    // ============================================
    // 플랫폼별 발행 통계
    // ============================================

    /*
     * 플랫폼별 발행 통계를 조회합니다.
     *
     * 지정된 기간의 블로그 플랫폼별 발행된 포스팅 수를 조회합니다.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @return 플랫폼별 발행 통계 리스트
     * */
    @Operation(summary = "플랫폼별 발행 통계 조회",
            description = "지정한 기간의 블로그 플랫폼별 발행된 포스팅 수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "플랫폼별 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (날짜 형식 오류, 시작일 > 종료일 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 전용)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/blog-platforms")
    public ResponseEntity<ApiResult<List<BlogPlatformStatisticsResponse>>> getBlogPlatformStatistics(
            @Parameter(
                    description = "조회 시작 날짜 (ISO-8601 형식)",
                    example = "2025-12-01",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "조회 종료 날짜 (ISO-8601 형식)",
                    example = "2025-12-31",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        List<BlogPlatformStatisticsResponse> statistics =
                statisticsService.getBlogPlatformStatistics(startDate, endDate);

        return ResponseEntity.ok(ApiResult.success("플랫폼별 발행 통계 조회 성공", statistics));
    }
}

