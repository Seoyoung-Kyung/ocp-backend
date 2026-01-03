package com.ocp.ocp_finalproject.content.controller;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.content.domain.ContentJob;
import com.ocp.ocp_finalproject.content.dto.ContentJobResponse;
import com.ocp.ocp_finalproject.content.dto.ContentRequestDto;
import com.ocp.ocp_finalproject.content.producer.ContentTaskProducer;
import com.ocp.ocp_finalproject.content.service.ContentJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentJobController {
    /*
    private final ContentJobService contentJobService;
    private final ContentTaskProducer contentTaskProducer;

    @PostMapping("/job")
    public ApiResult<ContentJobResponse> createJob(@Valid @RequestBody ContentRequestDto requestDto) {
        ContentJob job = contentJobService.createJob(requestDto);
        contentTaskProducer.sendTask(job.getJobId(), requestDto);
        return ApiResult.success(new ContentJobResponse(job));
    }

    @GetMapping("/job/{jobId}")
    public ApiResult<ContentJobResponse> getJob(@PathVariable String jobId) {
        ContentJob job = contentJobService.getJob(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Job not found: " + jobId));
        return ApiResult.success(new ContentJobResponse(job));
    }

     */
}
