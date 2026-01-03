package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import com.ocp.ocp_finalproject.workflow.dto.SetTrendCategoryNameDto;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkflowTestDetailResponse {

    private Long workflowId;
    private Long userId;
    private String userName;
    private String siteName;
    private String siteUrl;
    private String blogType;
    private String blogUrl;
    private String blogAccountId;
    private SetTrendCategoryNameDto setTrendCategory;
    private RecurrenceRuleDto recurrenceRule;
    private WorkflowStatus status;
    private WorkflowTestStatus testStatus;
    private TestWorkInfo latestWork;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TestWorkInfo {
        private Long workId;
        private WorkExecutionStatus status;
        private LocalDateTime completedAt;
        private String failureReason;
    }
}
