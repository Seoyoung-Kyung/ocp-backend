package com.ocp.ocp_finalproject.user.repository;

import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.domain.UserSuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSuspensionRepository extends JpaRepository<UserSuspension, Long> {

    /*
    * 활성화된 정지 이력 조회
    * */
    Optional<UserSuspension> findByUserAndIsActiveTrue(User user);

    /*
    * 사용자의 정지 여부 확인
    * */
    boolean existsByUserAndIsActiveTrue(User user);
}
