package com.ocp.ocp_finalproject.workflow.dto.request;

import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStatusRequest {

    @NotNull
    private WorkflowStatus newStatus;

}