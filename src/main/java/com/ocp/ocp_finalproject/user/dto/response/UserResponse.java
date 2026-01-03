package com.ocp.ocp_finalproject.user.dto.response;

import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private final Long userId;
    private final String name;
    private final String email;
    private final AuthProvider provider;
    private final UserRole role;
    private final UserStatus status;

    /***
     * User Entity -> UserResponse DTO 변환
     * @param user User 엔티티
     * @return UserResponse DTO
     */
    public static UserResponse from(User user, AuthProvider provider) {
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }

        return UserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(provider)
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
