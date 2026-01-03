package com.ocp.ocp_finalproject.siterequest.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.siterequest.domain.SiteRequest;
import com.ocp.ocp_finalproject.siterequest.dto.request.SiteRequestCreateDto;
import com.ocp.ocp_finalproject.siterequest.dto.response.SiteRequestResponse;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import com.ocp.ocp_finalproject.siterequest.repository.SiteRequestRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.INVALID_INPUT_VALUE;
import static com.ocp.ocp_finalproject.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteRequestServiceImpl implements SiteRequestService{

    private final SiteRequestRepository siteRequestRepository;
    private final UserRepository userRepository;
    private static final int PAGE_SIZE = 10;

    /**
     * 사이트 요청 생성
     * 개선: User 조회 제거 (Controller에서 User 직접 전달)
     */
    @Override
    @Transactional  // 쓰기 트랜잭션
    public SiteRequestResponse createRequest(Long userId, SiteRequestCreateDto dto) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 중복 URL 검증
        if (siteRequestRepository.existsBySiteUrl(dto.siteUrl())) {
            throw new CustomException(ErrorCode.DUPLICATE_SITE_URL);
        }

        // 사이트 요청 생성 및 저장
        SiteRequest siteRequest = SiteRequest.create(
                user,
                dto.siteUrl(),
                dto.siteName(),
                dto.description()
        );

        SiteRequest savedRequest = siteRequestRepository.save(siteRequest);

        log.info("사이트 요청 생성 완료 - userId: {}, siteUrl: {}", user.getId(), dto.siteUrl());

        return SiteRequestResponse.from(savedRequest);
    }

    /**
     * 내 요청 목록 조회
     */
    @Override
    public Page<SiteRequestResponse> getMyRequests(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SiteRequest> requests = siteRequestRepository.findByUserId(userId, pageable);

        return requests.map(SiteRequestResponse::from);
    }

    /**
     * 전체 요청 목록 조회 (관리자용)
     */
    @Override
    public Page<SiteRequestResponse> getAllRequests(int page, SiteRequestState state) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SiteRequest> requests;

        if (state != null) {
            requests = siteRequestRepository.findByState(state, pageable);
        } else {
            requests = siteRequestRepository.findAll(pageable);
        }

        return requests.map(SiteRequestResponse::from);
    }

    /**
     * 사이트 요청 승인
     */
    @Override
    @Transactional  // 쓰기 트랜잭션
    public SiteRequestResponse approveRequest(Long requestId) {
        SiteRequest request = siteRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "사이트 요청을 찾을 수 없습니다"));

        // 이미 처리된 요청인지 확인
        if (request.getState() != SiteRequestState.RECEIVED) {
            throw new CustomException(INVALID_INPUT_VALUE, "이미 처리된 요청입니다");
        }

        request.approve();

        log.info("사이트 요청 승인 완료 - requestId: {}, siteUrl: {}", requestId, request.getSiteUrl());

        return SiteRequestResponse.from(request);
    }

    /**
     * 사이트 요청 거부
     */
    @Override
    @Transactional
    public SiteRequestResponse rejectRequest(Long requestId) {
        SiteRequest request = siteRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "사이트 요청을 찾을 수 없습니다"));

        // 이미 처리된 요청인지 확인
        if (request.getState() != SiteRequestState.RECEIVED) {
            throw new CustomException(INVALID_INPUT_VALUE, "이미 처리된 요청입니다");
        }

        request.reject();

        log.info("사이트 요청 거부 완료 - requestId: {}, siteUrl: {}", requestId, request.getSiteUrl());

        return SiteRequestResponse.from(request);
    }
}
