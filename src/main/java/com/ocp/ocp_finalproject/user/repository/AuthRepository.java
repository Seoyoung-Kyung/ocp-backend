package com.ocp.ocp_finalproject.user.repository;

import com.ocp.ocp_finalproject.user.domain.Auth;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    /**
     * Provider & ProviderUserId 인증 정보 조회
     */
    Optional<Auth> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    /**
     * 사용자에 대한 최근 인증 정보 조회
     */
    Optional<Auth> findTopByUserOrderByCreatedAtDesc(User user);
}
