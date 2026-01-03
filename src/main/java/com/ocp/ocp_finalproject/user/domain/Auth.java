package com.ocp.ocp_finalproject.user.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_auth_provider_user",
           columnNames = {"provider", "provider_user_id"}
       ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private AuthProvider provider;

    @Column(name = "provider_user_id", length = 100)
    private String providerUserId;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder(builderMethodName = "createBuilder")
    public static Auth create(User user, AuthProvider provider, String providerUserId) {
        Auth auth = new Auth();
        auth.user = user;
        auth.provider = provider;
        auth.providerUserId = providerUserId;
        auth.recordLogin(); // 생성시 자동 로그인 기록
        return auth;
    }

    // === 비즈니스 메서드 ===
    /**
     * 로그인 시간 기록
     */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // === 조회 메서드 ===
    /**
     * 신규 가입 여부 확인
     * 로그인 시간과 생성 시간이 1분 이내면 신규 가입으로 판단
     */
    public boolean isNewUser(){
        if(lastLoginAt == null || getCreatedAt() == null) {
            return false;
        }

        long diffSeconds = Duration.between(getCreatedAt(), lastLoginAt).getSeconds();

        return diffSeconds < 60;
    }
}
