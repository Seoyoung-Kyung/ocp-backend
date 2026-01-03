package com.ocp.ocp_finalproject.workflow.dto.response;

import com.ocp.ocp_finalproject.workflow.enums.WorkflowTestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class WorkflowTestResponse {

    private Long workflowId;

    private Boolean contentGenerateRequested;

    private Integer blogUploadCount;

    private String message;

    private WorkflowTestStatus testStatus;
}