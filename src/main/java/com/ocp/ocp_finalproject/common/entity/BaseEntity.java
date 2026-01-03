package com.ocp.ocp_finalproject.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Getter: Lombok - 모든 필드의 getter 메서드 자동 생성
@Getter
// MappedSuperclass: 이 클래스를 상속받는 Entity들이 이 클래스의 필드를 컬럼으로 인식하게 함
// 테이블은 안 만들어지고, 상속받는 Entity 테이블에 컬럼이 추가됨
@MappedSuperclass
// EntityListeners: Entity의 변화를 감지하는 리스너 등록
// AuditingEntityListener: 생성/수정 시간을 자동으로 넣어주는 JPA Auditing 리스너
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // CreatedDate: Entity가 생성될 때 시간을 자동으로 저장
    @CreatedDate
    // updatable = false: 한 번 생성된 후에는 수정 불가 (생성 시간은 바뀌면 안 되니까)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // LastModifiedDate: Entity가 수정될 때마다 시간을 자동으로 업데이트
    @LastModifiedDate
    private LocalDateTime updatedAt;
}