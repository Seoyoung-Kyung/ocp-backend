package com.ocp.ocp_finalproject.trend.repository;

import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import com.ocp.ocp_finalproject.workflow.dto.response.TrendCategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrendCategoryRepository extends JpaRepository<TrendCategory, Long> {

    @Query("SELECT tc FROM TrendCategory tc " +
            "LEFT JOIN FETCH tc.parentCategory p1 " +
            "LEFT JOIN FETCH p1.parentCategory p2 " +
            "WHERE tc.id = :categoryId")
    Optional<TrendCategory> findCategoryWithParent(@Param("categoryId") Long categoryId);

    @Query("SELECT tc FROM TrendCategory tc " +
            "LEFT JOIN FETCH tc.parentCategory p1 " +
            "LEFT JOIN FETCH p1.parentCategory p2")
    List<TrendCategory> findByParentCategoryIsNull();
}