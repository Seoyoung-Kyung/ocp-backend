package com.ocp.ocp_finalproject.work.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.message.blog.BlogUploadProducer;
import com.ocp.ocp_finalproject.message.blog.dto.BlogUploadRequest;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import com.ocp.ocp_finalproject.work.service.BlogUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class BlogUploadTriggerController {

    private final BlogUploadService blogUploadService;
    private final BlogUploadProducer blogUploadProducer;

    @PostMapping("/{workflowId}/blog-upload")
    public ResponseEntity<ApiResult<Void>> blogUpload(@PathVariable Long workflowId) {
        List<BlogUploadRequest> requests = blogUploadService.collectPendingBlogUploadsForWorkflow(workflowId);

        for (BlogUploadRequest request : requests) {
            BlogUploadRequest prepared = blogUploadService.prepareBlogUploadRequest(request);
            blogUploadProducer.send(prepared);
            log.info("워크 {} 블로그 업로드 메시지 전송", prepared.getWorkId());
        }
        return ResponseEntity.ok(ApiResult.success("블로그 업로드 요청을 전송했습니다."));
    }
}
