package com.ocp.ocp_finalproject.scheduler.job;

import com.ocp.ocp_finalproject.message.blog.BlogUploadProducer;
import com.ocp.ocp_finalproject.message.blog.dto.BlogUploadRequest;
import com.ocp.ocp_finalproject.work.service.BlogUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BlogUploadJob implements Job {


    private final BlogUploadService blogUploadService;

    private final BlogUploadProducer blogUploadProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long workflowId = context.getMergedJobDataMap().getLong("workflowId");

        log.info("Workflow {} 블로그 업로드 스케줄 실행", workflowId);

        // 해당 workflow에 대해 업로드 요청만 생성
        List<BlogUploadRequest> requests = blogUploadService.collectPendingBlogUploadsForWorkflow(workflowId);

        for (BlogUploadRequest request : requests) {
            BlogUploadRequest prepared = blogUploadService.prepareBlogUploadRequest(request);
            blogUploadProducer.send(prepared);
            log.info("워크 {} 블로그 업로드 메시지 전송", prepared.getWorkId());
        }
    }
}
