package com.ocp.ocp_finalproject.user.service;

import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.Auth;
import com.ocp.ocp_finalproject.user.enums.AuthProvider;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import com.ocp.ocp_finalproject.user.repository.AuthRepository;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    /**
     * User Id로 조회
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));
    }

    /**
     * email로 조회
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다. email: " + email));
    }

    /**
     * 사용자 상태 변경
     */
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = getUserById(userId);
        user.updateStatus(status);

        log.info("사용자 상태 변경 - userId: {} status: {} -> {}", userId, user.getStatus(), status);
    }

    /**
     * 사용자 비활성 상태로 변경 ( 로그아웃 시 사용 )
     */
    @Transactional
    public void deactivateUser(Long userId) {
        updateUserStatus(userId, UserStatus.INACTIVE);
        log.info("사용자 비활성화 - userId: {}", userId);
    }

    /**
     * 사용자 활성 상태로 변경 ( 로그인 시 사용 )
     */
    @Transactional
    public void activateUser(Long userId) {
        updateUserStatus(userId, UserStatus.ACTIVE);
    }

    /**
     * 회원 탈퇴 처리
     * - 실제 데이터는 삭제하지 않음
     */
    @Transactional
    public void withdrawUser(Long userId) {
        User user = getUserById(userId);

        if(user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다..");
        }

        user.updateStatus(UserStatus.WITHDRAWN);
        log.info("회원 탈퇴 처리 완료 - userId: {}", userId);
    }

    /**
     * 사용자 인증 제공자 조회
     */
    @Transactional(readOnly = true)
    public AuthProvider getUserProvider(User user) {
        return authRepository.findTopByUserOrderByCreatedAtDesc(user)
                .map(Auth::getProvider)
                .orElse(null);
    }
}
