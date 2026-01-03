package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BlogTypeResponse {

    private Long blogTypeId;

    private String blogTypeName;

    private String blogBaseUrl;

    public static BlogTypeResponse from(BlogType blogType) {
        return BlogTypeResponse.builder()
                .blogTypeId(blogType.getId())
                .blogTypeName(blogType.getBlogTypeName())
                .blogBaseUrl(blogType.getBaseUrl())
                .build();
    }

}