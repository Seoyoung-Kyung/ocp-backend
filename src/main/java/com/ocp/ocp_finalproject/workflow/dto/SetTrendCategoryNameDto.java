package com.ocp.ocp_finalproject.workflow.dto;

import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SetTrendCategoryNameDto {

    private String depth1Category;

    private String depth2Category;

    private String depth3Category;

    public static SetTrendCategoryNameDto from(TrendCategory category) {
        TrendCategory parent = category.getParentCategory();
        TrendCategory grandParent = (parent != null) ? parent.getParentCategory() : null;

        String depth1Name;
        String depth2Name;
        String depth3Name;

        if (parent == null) {
            // 부모가 없으면 category가 depth1
            depth1Name = category.getTrendCategoryName();
            depth2Name = null;
            depth3Name = null;
        } else if (grandParent == null) {
            // 조부모가 없으면 category가 depth2
            depth1Name = parent.getTrendCategoryName();
            depth2Name = category.getTrendCategoryName();
            depth3Name = null;
        } else {
            // 조부모가 있으면 category가 depth3
            depth1Name = grandParent.getTrendCategoryName();
            depth2Name = parent.getTrendCategoryName();
            depth3Name = category.getTrendCategoryName();
        }

        return SetTrendCategoryNameDto.builder()
                .depth1Category(depth1Name)
                .depth2Category(depth2Name)
                .depth3Category(depth3Name)
                .build();
    }

}