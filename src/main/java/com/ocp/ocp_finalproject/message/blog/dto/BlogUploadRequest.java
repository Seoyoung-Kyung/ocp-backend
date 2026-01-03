package com.ocp.ocp_finalproject.message.blog.dto;

import lombok.Data;

@Data
public class BlogUploadRequest {
    private Long workId;
    private String blogType;
    private String title;
    private String content;
    private String blogId;
    private String blogPassword;
    private String blogUrl;
    private String webhookUrl;
    private String webhookToken;
    private Boolean isTest;
}
