package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AdminWorkflowListResponse {

    private final Long workflowId;

    private final Long userId;

    private final String userName;

    private String siteName;

    private final String siteUrl;

    private final String trendCategoryName;

    private final String blogType;

    private final String blogAccountId;

    private final String blogUrl;

    private final String readableRule;

    private final WorkflowStatus status;

    private WorkflowTestStatus testStatus;
}
