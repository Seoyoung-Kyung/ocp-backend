package com.ocp.ocp_finalproject.workflow.dto;

import com.ocp.ocp_finalproject.trend.domain.TrendCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SetTrendCategoryIdDto {

    private Long depth1Category;

    private Long depth2Category;

    private Long depth3Category;

    public static SetTrendCategoryIdDto from(TrendCategory category) {
        TrendCategory parent = category.getParentCategory();
        TrendCategory grandParent = (parent != null) ? parent.getParentCategory() : null;

        Long depth1Id;
        Long depth2Id;
        Long depth3Id;

        if (parent == null) {
            // 부모가 없으면 category가 depth1
            depth1Id = category.getId();
            depth2Id = null;
            depth3Id = null;
        } else if (grandParent == null) {
            // 조부모가 없으면 category가 depth2
            depth1Id = parent.getId();
            depth2Id = category.getId();
            depth3Id = null;
        } else {
            // 조부모가 있으면 category가 depth3
            depth1Id = grandParent.getId();
            depth2Id = parent.getId();
            depth3Id = category.getId();
        }

        return SetTrendCategoryIdDto.builder()
                .depth1Category(depth1Id)
                .depth2Category(depth2Id)
                .depth3Category(depth3Id)
                .build();
    }
}