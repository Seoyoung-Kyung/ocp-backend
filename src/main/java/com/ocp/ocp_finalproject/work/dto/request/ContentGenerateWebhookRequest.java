package com.ocp.ocp_finalproject.work.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentGenerateWebhookRequest {

    @JsonProperty("workId")
    private Long workId;

    private Boolean success;

    private String message;

    private String errorType;

    private String step;

    @JsonProperty("completedAt")
    private OffsetDateTime completedAt;

    private String title;
    private String content;
    private String summary;

    @JsonProperty("isTest")
    private Boolean isTest;
}
