package com.ocp.ocp_finalproject.work.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class KeywordSelectWebhookRequest {

    @JsonProperty("workId")
    private Long workId;

    private String keyword;

    private boolean success;

    private String message;

    @JsonProperty("startedAt")
    private OffsetDateTime startedAt;

    @JsonProperty("completedAt")
    private OffsetDateTime completedAt;

    @JsonProperty("isTest")
    private Boolean isTest;
}
