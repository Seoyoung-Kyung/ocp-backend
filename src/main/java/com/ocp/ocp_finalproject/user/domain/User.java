package com.ocp.ocp_finalproject.user.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private UserRole role;

    // OAuth2 로그인으로 신규 사용자 생성
    @Builder(builderMethodName = "createBuilder")
    public static User create(String name, String email) {
        User user = new User();
        user.name = name;
        user.email = email;
        user.status = UserStatus.ACTIVE;
        user.role = UserRole.USER;
        return user;
    }

    // ================ 비즈니스 매서드 ================
    /**
     * 사용자 정보 수정
     */
    public void updateProfile(String name, String email){
        this.name = name;
        this.email = email;
    }

    /**
     * 계정 상태 변경
     */
    public void updateStatus(UserStatus status){
        this.status = status;
    }

    /**
     * 권한 변경
     */
    public void updateRole(UserRole role){
        this.role = role;
    }
}
