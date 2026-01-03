package com.ocp.ocp_finalproject.notice.service;

import com.ocp.ocp_finalproject.notice.dto.request.NoticeCreateRequest;
import com.ocp.ocp_finalproject.notice.dto.request.NoticeUpdateRequest;
import com.ocp.ocp_finalproject.notice.dto.response.NoticeResponse;

import java.util.List;

public interface NoticeService {

    List<NoticeResponse> getAllNotice();

    NoticeResponse getNotice(Long noticeId);

    NoticeResponse createNotice(NoticeCreateRequest request, Long authorId);

    void deleteNotice(Long noticeId);

    NoticeResponse updateNotice(Long noticeId, NoticeUpdateRequest request);

}
