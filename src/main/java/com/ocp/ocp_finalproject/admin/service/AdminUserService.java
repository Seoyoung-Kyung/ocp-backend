package com.ocp.ocp_finalproject.admin.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserSuspension;
import com.ocp.ocp_finalproject.user.enums.UserRole;
import com.ocp.ocp_finalproject.user.enums.UserStatus;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import com.ocp.ocp_finalproject.user.repository.UserSuspensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserSuspensionRepository userSuspensionRepository;

    /*
    * 전체 사용자 조회(페이징)
    * */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /*
    * 사용자 상세 조회
    * */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    /*
     * 사용자 정지 이력 조회
     * */
    public UserSuspension getActiveSuspension(User user) {
        return userSuspensionRepository.findByUserAndIsActiveTrue(user).orElse(null);
    }

    /*
    * 사용자 활동 정지
    * */
    @Transactional
    public void suspendUser(Long userId, Long adminUserId, String reason) {
        User user = getUserById(userId);
        User admin = getUserById(adminUserId);

        // 자기 자신을 정지할 수 없음
        if (userId.equals(adminUserId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "자기 자신을 정지할 수 없습니다.");
        }

        // 관리자 계정은 정지할 수 없음
        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "관리자 계정은 정지할 수 없습니다.");
        }

        // 이미 정지된 사용자인지 확인
        if(user.getStatus() == UserStatus.SUSPENDED){
            throw new CustomException(ErrorCode.ALREADY_SUSPENDED);
        }

        // 사용자 상태 변경
        user.updateStatus(UserStatus.SUSPENDED);

        // 정지 이력 생성
        UserSuspension suspension = UserSuspension.createBuilder()
                .user(user)
                .suspendedUser(admin)
                .reason(reason)
                .suspendedAt(LocalDateTime.now())
                .unsuspendedAt(null)
                .isActive(true)
                .build();

        userSuspensionRepository.save(suspension);
        log.info("사용자 정지 완료 - userId: {}, admin: {}, reason: {}", userId, adminUserId, reason);
    }

    /*
    * 사용자 활동 정지 해제
    * */
    @Transactional
    public void unsuspendUser(Long userId) {
        User user = getUserById(userId);

        // 정지된 사용자가 아니면 에러
        if (user.getStatus() != UserStatus.SUSPENDED){
            throw new CustomException(ErrorCode.NOT_SUSPENDED);
        }

        // 활성화된 정지 이력 찾기
        UserSuspension suspension = userSuspensionRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new CustomException(ErrorCode.SUSPENSION_NOT_FOUND));

        // 사용자 상태를 ACTIVE로 변경
        user.updateStatus(UserStatus.ACTIVE);

        // 정지 이력 비활성화
        suspension.deactivate();

        log.info("사용자 정지 해제 완료 - userId: {}", userId);
    }

    /*
     * 사용자 검색 (복합 조건)
     * @param name 이름 (부분 검색)
     * @param email 이메일 (부분 검색)
     * @param status 계정 상태
     * @param role 권한
     * @param pageable 페이징 정보
     * @return 검색된 사용자 목록
     */
    public Page<User> searchUsers(String name, String email, UserStatus userStatus, UserRole userRole, Pageable pageable) {
        return userRepository.searchUsers(name, email, userStatus, userRole, pageable);
    }
}
