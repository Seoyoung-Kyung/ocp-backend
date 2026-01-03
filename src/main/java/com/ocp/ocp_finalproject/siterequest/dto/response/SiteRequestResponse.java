package com.ocp.ocp_finalproject.siterequest.dto.response;

import com.ocp.ocp_finalproject.siterequest.domain.SiteRequest;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;

import java.time.LocalDateTime;

public record SiteRequestResponse(
        Long requestId,
        String siteUrl,
        String siteName,
        SiteRequestState state,
        String description,
        LocalDateTime createdAt
) {
    public static SiteRequestResponse from(SiteRequest siteRequest) {
        return new SiteRequestResponse(
                siteRequest.getId(),
                siteRequest.getSiteUrl(),
                siteRequest.getSiteName(),
                siteRequest.getState(),
                siteRequest.getDescription(),
                siteRequest.getCreatedAt()
        );
    }
}
