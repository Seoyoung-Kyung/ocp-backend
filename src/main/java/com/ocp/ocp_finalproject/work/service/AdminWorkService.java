package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.user.domain.UserPrincipal;
import com.ocp.ocp_finalproject.work.dto.response.AdminWorkPageResponse;

public interface AdminWorkService {

    AdminWorkPageResponse getWorksForAdmin(UserPrincipal principal, Long workflowId, int page);
}