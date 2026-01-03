package com.ocp.ocp_finalproject.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPlatformStatisticsResponse {

    /**
     * 블로그 플랫폼 이름
     * 예: "티스토리", "네이버"
     */
    private String platformName;

    /**
     * 발행된 포스팅 수
     */
    private Long postCount;

    /**
     * Object[] 쿼리 결과를 DTO로 변환
     *
     * @param result [String platformName, Long postCount]
     * @return BlogPlatformStatisticsResponse
     */
    public static BlogPlatformStatisticsResponse from(Object[] result) {
        return BlogPlatformStatisticsResponse.builder()
                .platformName((String) result[0])
                .postCount(((Number) result[1]).longValue())
                .build();
    }
}
