package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.workflow.dto.SetTrendCategoryIdDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkflowResponse {

    private Long workflowId;

    private Long userId;

    private String siteUrl;

    private String blogType;

    private String blogUrl;

    private SetTrendCategoryIdDto setTrendCategory;

    private String blogAccountId;

    private String readableRule;

}