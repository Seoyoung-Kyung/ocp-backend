package com.ocp.ocp_finalproject.admin.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "common_code_group")
public class CommonCodeGroup extends BaseEntity {

    @Id
    @Column(name = "group_id", length = 50)
    private String id;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(length = 255)
    private String description;

    @OneToMany(
            mappedBy = "commonCodeGroup",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CommonCode> commonCodes = new ArrayList<>();

    // 정적 팩토리 + Builder
    @Builder(builderMethodName = "createBuilder")
    public static CommonCodeGroup create(
            String groupId,
            String groupName,
            String description,
            List<CommonCode> codes
    ) {
        CommonCodeGroup group = new CommonCodeGroup();
        group.id = groupId;
        group.groupName = groupName;
        group.description = description;
        if (codes != null && !codes.isEmpty()) {
            codes.forEach(group::addCode);  // Builder 안에서 안전하게 추가
        }
        return group;
    }


    // 편의 메서드: CommonCode를 안전하게 추가
    public void addCode(CommonCode code) {
        if (!commonCodes.contains(code)) {
            commonCodes.add(code);
        }
        code.setCommonCodeGroup(this);
    }

    // 비즈니스 메서드 작성
    // 그룹 정보 수정
    public void updateInfo(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
    }
}
