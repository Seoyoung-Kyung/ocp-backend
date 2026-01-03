package com.ocp.ocp_finalproject.siterequest.service;

import com.ocp.ocp_finalproject.siterequest.dto.request.SiteRequestCreateDto;
import com.ocp.ocp_finalproject.siterequest.dto.response.SiteRequestResponse;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import org.springframework.data.domain.Page;

public interface SiteRequestService {
    SiteRequestResponse createRequest(Long userId, SiteRequestCreateDto dto);
    Page<SiteRequestResponse> getMyRequests(Long userId, int page);
    Page<SiteRequestResponse> getAllRequests(int page, SiteRequestState state);

    /**
     * 사이트 요청 승인
     */
    SiteRequestResponse approveRequest(Long requestId);

    /**
     * 사이트 요청 거부
     */
    SiteRequestResponse rejectRequest(Long requestId);
}
