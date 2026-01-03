package com.ocp.ocp_finalproject.admin.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "common_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode extends BaseEntity {

    @Id
    @Column(name = "code_id", length = 50)
    private String id;

    @Column(name = "code_name", length = 100, nullable = false)
    private String codeName;

    @Column(length = 255)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private CommonCodeGroup commonCodeGroup;

    // 정적 팩토리 + Builder
    @Builder(builderMethodName = "createBuilder")
    public static CommonCode create(
            String codeId,
            CommonCodeGroup commonCodeGroup,
            String codeName,
            String description,
            Integer sortOrder,
            Boolean isActive
    ) {
        CommonCode code = new CommonCode();
        code.id = codeId;
        code.codeName = codeName;
        code.description = description;
        code.sortOrder = sortOrder;
        code.isActive = isActive;
        code.commonCodeGroup = commonCodeGroup;  // 단방향 설정만
        return code;
    }

    // 편의 메서드: CommonCodeGroup과 양방향 안전하게 연결
    public void setCommonCodeGroup(CommonCodeGroup group) {
        this.commonCodeGroup = group;
        if (!group.getCommonCodes().contains(this)) {
            group.getCommonCodes().add(this);
        }
    }

    // 비즈니스 메서드 작성
    /**
     * 활성화 상태 변경
     */
    public void updateActive(Boolean isActive)
    {
        this.isActive = isActive;
    }

    /**
     * 코드 정보를 한번에 수정
     */
    public void updateInfo(String codeName, String description, Integer sortOrder)
    {
        this.codeName = codeName;
        this.description = description;
        this.sortOrder = sortOrder;
    }
    /*
    엔티티에 비즈니스 로직을 넣는 이유는:
    1. 캡슐화: 데이터 변경 로직을 엔티티 내부에 숨김
    2. 의도 표현: setIsActive(false) 보다 updateActive(false)가 더 명확
    3. 유지보수성: 변경 로직이 한곳에 모여있어 수정이 쉬움
    4. 확장성: 나중에 로직 추가 시 메서드만 수정하면 됨*/
}
