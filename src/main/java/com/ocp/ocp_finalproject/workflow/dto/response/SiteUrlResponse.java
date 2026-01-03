package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.workflow.enums.SiteUrlInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class SiteUrlResponse {

    private final String siteName;       // "무신사" (화면 표시용)
    private final String siteUrl;      // "musinsa.com"

    public static SiteUrlResponse from(SiteUrlInfo site) {
        return SiteUrlResponse.builder()
                .siteName(site.getSiteName())
                .siteUrl(site.getSiteUrl())
                .build();
    }
}
