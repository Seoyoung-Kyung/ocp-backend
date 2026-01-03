package com.ocp.ocp_finalproject.workflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 쇼핑몰 사이트 URL 정보
 */
@Getter
@RequiredArgsConstructor
public enum SiteUrlInfo {

    MUSINSA("무신사", "https://www.musinsa.com"),
    GMARKET("G마켓", "https://www.gmarket.co.kr"),
    TWENTY_NINE_CM("29CM", "https://www.29cm.co.kr"),
    OLIVEYOUNG("올리브영", "https://www.oliveyoung.co.kr"),
    ELEVEN_ST("11번가", "https://www.11st.co.kr"),
    SSG("SSG", "https://www.ssg.com"),
    EQL("EQL", "https://www.eql.kr"),
    KREAM("크림", "https://kream.co.kr"),
    ZIGZAG("지그재그", "https://www.zigzag.kr"),
    SSADAGU("싸다구", "https://www.ssadagu.kr");

    private final String siteName;
    private final String siteUrl;

    /**
     * URL로 SiteUrlInfo 찾기
     */
    public static SiteUrlInfo fromUrl(String url) {
        for (SiteUrlInfo site : values()) {
            if (url != null && url.contains(extractDomain(site.getSiteUrl()))) {
                return site;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 사이트 URL입니다: " + url);
    }

    /**
     * 사이트명으로 SiteUrlInfo 찾기
     */
    public static SiteUrlInfo fromSiteName(String siteName) {
        for (SiteUrlInfo site : values()) {
            if (site.getSiteName().equals(siteName)) {
                return site;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 사이트명입니다: " + siteName);
    }

    public static String getSiteNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "알 수 없는 사이트";
        }

        try {
            return fromUrl(url).getSiteName();
        } catch (IllegalArgumentException e) {
            return "알 수 없는 사이트";
        }
    }

    /**
     * URL에서 도메인 추출 (예: https://www.example.com -> example.com)
     */
    private static String extractDomain(String url) {
        return url.replaceAll("https?://(www\\.)?", "").split("/")[0];
    }
}