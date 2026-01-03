package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.workflow.dto.request.WorkflowRequest;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowTestDetailResponse;
import com.ocp.ocp_finalproject.workflow.dto.response.WorkflowTestResponse;
import org.quartz.SchedulerException;

public interface WorkflowTestService {

    WorkflowTestResponse executeWorkflowTest(UserPrincipal userPrincipal, WorkflowRequest workflowRequest, Long replaceWorkflowId) throws SchedulerException;

    WorkflowTestDetailResponse getTestWorkflow(Long workflowId, UserPrincipal principal);
}
