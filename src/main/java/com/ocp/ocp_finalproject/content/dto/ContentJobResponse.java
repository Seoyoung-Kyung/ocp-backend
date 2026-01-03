package com.ocp.ocp_finalproject.content.dto;

import com.ocp.ocp_finalproject.content.domain.ContentJob;
import com.ocp.ocp_finalproject.content.domain.ContentJobStatus;
import lombok.Getter;

@Getter
public class ContentJobResponse {
    private final String jobId;
    private final String taskType;
    private final ContentJobStatus status;

    public ContentJobResponse(ContentJob job) {
        this.jobId = job.getJobId();
        this.taskType = job.getTaskType();
        this.status = job.getStatus();
    }
}
