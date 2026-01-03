package com.ocp.ocp_finalproject.user.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_suspension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSuspension extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suspension_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suspended_user_id", nullable = false)
    private User suspendedUser;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "suspended_at")
    private LocalDateTime suspendedAt;

    @Column(name = "unsuspended_at")
    private LocalDateTime unsuspendedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder(builderMethodName = "createBuilder")
    public static UserSuspension create(User user, User suspendedUser, String reason, LocalDateTime suspendedAt, LocalDateTime unsuspendedAt, Boolean isActive) {
        UserSuspension userSuspension = new UserSuspension();
        userSuspension.user = user;
        userSuspension.suspendedUser = suspendedUser;
        userSuspension.reason = reason;
        userSuspension.suspendedAt = suspendedAt;
        userSuspension.unsuspendedAt = unsuspendedAt;
        userSuspension.isActive = (isActive != null) ? isActive : true;
        return userSuspension;
    }

    // 비즈니스 메서드
    /*
    * 정지 해제
    * */
    public void deactivate(){
        this.isActive = false;
        this.unsuspendedAt = LocalDateTime.now();
    }
}
