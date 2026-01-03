package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TrendCategoryResponse {

    private Long categoryId;

    private String categoryName;

    private List<TrendCategoryResponse> children;

    public static TrendCategoryResponse from(TrendCategory category) {
        return TrendCategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getTrendCategoryName())
                .children(category.getChildrenCategory()
                        .stream()
                        .map(TrendCategoryResponse::from) // 재귀 호출
                        .toList())
                .build();
    }

}