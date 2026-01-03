package com.ocp.ocp_finalproject.work.dto.response;

import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminWorkListResponse {

    private Long workId;

    private Long workflowId;

    private Long userId;

    private String postingUrl;

    private String title;

    private String content;

    private String choiceTrendKeyword;

    private LocalDateTime completedAt;

    private String choiceProduct;

    private WorkExecutionStatus status;

    private String failureReason;
}
