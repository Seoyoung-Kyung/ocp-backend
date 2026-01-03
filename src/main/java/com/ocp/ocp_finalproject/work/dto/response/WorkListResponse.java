package com.ocp.ocp_finalproject.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkListResponse {
    private Long workId;
    private String status;
    private String postingUrl;
    private LocalDateTime completedAt;
    private String choiceProduct;
    private String failureReason;
}
