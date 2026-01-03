package com.ocp.ocp_finalproject.work.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlogUploadWebhookRequest {

    @JsonProperty("workId")
    private Long workId;

    private String platform;

    private boolean success;

    private String status;

    @JsonProperty("postingUrl")
    private String postingUrl;

    private String message;

    private Map<String, Object> metadata;

    @JsonProperty("completedAt")
    private OffsetDateTime completedAt;

    @JsonProperty("isTest")
    private Boolean isTest;
}
