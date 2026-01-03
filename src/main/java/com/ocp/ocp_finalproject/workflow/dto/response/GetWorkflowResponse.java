package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import com.ocp.ocp_finalproject.workflow.dto.SetTrendCategoryNameDto;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetWorkflowResponse {

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
}