package com.ocp.ocp_finalproject.trend.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trend_category")
public class TrendCategory extends BaseEntity {

    @Id
    @Column(name = "trend_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private TrendCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<TrendCategory> childrenCategory = new ArrayList<>();

    @Column(name = "trend_category_name", length = 100)
    private String trendCategoryName;

    @Column(name = "depth")
    private Integer depth;

    @Builder(builderMethodName = "createBuilder")
    public static TrendCategory create(TrendCategory parentCategory, String trendCategoryName, Integer depth) {
        TrendCategory trendCategory = new TrendCategory();
        trendCategory.parentCategory = parentCategory;
        trendCategory.trendCategoryName = trendCategoryName;
        trendCategory.depth = depth;
        return trendCategory;
    }

    public void updateInfo(String trendCategoryName, Integer depth, TrendCategory parentCategory) {
        this.trendCategoryName = trendCategoryName;
        this.depth = depth;
        this.parentCategory = parentCategory;
    }

    public List<TrendCategory> getFullPath() {
        List<TrendCategory> path = new ArrayList<>();
        TrendCategory current = this;
        while (current != null) {
            path.add(0, current);
            current = current.getParentCategory();
        }
        return path;
    }
}