package com.ocp.ocp_finalproject.workflow.dto;

import com.ocp.ocp_finalproject.blog.domain.BlogType;
import lombok.Getter;

@Getter
public class UserBlogDto {

    private BlogType blogType;

    private String accountId;

    private String blogUrl;
}
