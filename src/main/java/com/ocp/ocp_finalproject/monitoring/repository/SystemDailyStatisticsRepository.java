package com.ocp.ocp_finalproject.monitoring.repository;

import com.ocp.ocp_finalproject.monitoring.domain.SystemDailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SystemDailyStatisticsRepository extends JpaRepository<SystemDailyStatistics, Long> {

    /*
    * 일별 통계 조회 (기간)
    *
    * 날짜 범위로 조회하여 인덱스 활용 가능 (SARGable)
    * YEAR(), MONTH() 같은 함수를 WHERE 절에서 사용하지 않음
    *
    * @param startDate 시작 날짜 (포함)
    * @param endDate 종료 날짜 (포함)
    * @return 기간 내 일별 통계 리스트 (날짜 오름차순)
    * */
    List<SystemDailyStatistics> findByStatDateBetweenOrderByStatDateAsc(LocalDate startDate, LocalDate endDate);

    /*
    * 특정 날짜의 통계 조회
    *
    * 단일 날짜로 직접 조회하여 효율적
    *
    * @param date 조회할 날짜
    * @return 해당 날짜의 통계 (Optional)
    * */
    Optional<SystemDailyStatistics> findByStatDate(LocalDate date);

    /*
    * 특정 날짜의 통계 존재 여부 확인
    *
    * @param date 확인할 날짜
    * @return 존재 여부
    * */
    boolean existsByStatDate(LocalDate date);

    /*
    * 특정 날짜의 통계 삭제
    *
    * 통계 재집계 시 기존 데이터를 삭제하기 위해 사용
    *
    * @param date 삭제할 날짜
    * */
    void deleteByStatDate(LocalDate date);
}
