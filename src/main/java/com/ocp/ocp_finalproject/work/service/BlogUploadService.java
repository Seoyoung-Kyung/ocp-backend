package com.ocp.ocp_finalproject.work.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocp.ocp_finalproject.blog.domain.BlogType;
import com.ocp.ocp_finalproject.blog.domain.UserBlog;
import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.message.blog.dto.BlogUploadRequest;
import com.ocp.ocp_finalproject.work.config.BlogUploadProperties;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.workflow.domain.RecurrenceRule;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ocp.ocp_finalproject.workflow.util.AesCryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogUploadService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final BlogUploadProperties blogUploadProperties;
    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;
    private final ObjectMapper objectMapper;
    private final AesCryptoUtil aesCryptoUtil;
    @Transactional(readOnly = true)
    public List<BlogUploadRequest> collectPendingBlogUploadsForWorkflow(Long workflowId) {

        // 1. 특정 워크플로우의 CONTENT_GENERATED 상태인 Work만 조회
        List<Work> works = workRepository
                .findWorksWithBlog(workflowId, WorkExecutionStatus.CONTENT_GENERATED);

        if (works.isEmpty()) {
            return Collections.emptyList();
        }

        List<BlogUploadRequest> requests = new ArrayList<>();

        for (Work work : works) {

            // 2. AI 콘텐츠 조회
            Optional<AiContent> aiContentOpt = aiContentRepository.findTopByWorkId(work.getId());
            if (aiContentOpt.isEmpty()) {
                log.warn("워크 {} : AI 콘텐츠가 없어 업로드 스킵", work.getId());
                continue;
            }
            AiContent aiContent = aiContentOpt.get();

            // 3. 블로그 정보 조회
            Workflow workflow = work.getWorkflow();
            UserBlog blog = workflow.getUserBlog();
            if (blog == null) {
                log.warn("워크 {} : 블로그 정보 없음 -> 업로드 스킵", work.getId());
                continue;
            }
            boolean isTestWorkflow = workflow.getStatus() == WorkflowStatus.PRE_REGISTERED;

            // 4. 업로드 요청 생성
            BlogUploadRequest req = new BlogUploadRequest();
            req.setWorkId(work.getId());
            req.setTitle(aiContent.getTitle());
            req.setContent(aiContent.getContent());
            req.setBlogType(resolveBlogType(blog.getBlogType()));
            req.setBlogId(blog.getAccountId());
            req.setBlogPassword(aesCryptoUtil.decrypt(blog.getAccountPassword()));
            req.setBlogUrl(blog.getBlogUrl());
            req.setIsTest(isTestWorkflow);

            requests.add(req);
        }

        return requests;
    }

    public BlogUploadRequest prepareBlogUploadRequest(BlogUploadRequest request) {
        applyDefaultWebhookUrlIfNeeded(request);
        applyWebhookToken(request);
        return request;
    }

    private String resolveBlogType(BlogType blogType) {
        if (blogType == null || blogType.getBlogTypeName() == null) {
            return "unknown";
        }
        String normalized = blogType.getBlogTypeName().trim().toLowerCase();
        if (normalized.contains("naver") || normalized.contains("네이버")) {
            return "naver";
        }
        if (normalized.contains("tistory") || normalized.contains("티스토리")) {
            return "tistory";
        }
        return normalized.replace(" ", "");
    }

    private void applyWebhookToken(BlogUploadRequest request) {
        String secret = blogUploadProperties.getWebhookSecret();
        if (secret == null || secret.isBlank()) {
            throw new CustomException(ErrorCode.WORK_WEBHOOK_TOKEN_INVALID, "웹훅 시크릿이 설정되지 않았습니다.");
        }
        request.setWebhookToken(secret);
    }

    private void applyDefaultWebhookUrlIfNeeded(BlogUploadRequest request) {
        if (request.getWebhookUrl() != null && !request.getWebhookUrl().isBlank()) {
            return;
        }

        String defaultWebhookUrl = blogUploadProperties.getWebhookUrl();
        if (defaultWebhookUrl == null || defaultWebhookUrl.isBlank()) {
            throw new CustomException(ErrorCode.WORK_WEBHOOK_URL_NOT_CONFIGURED, "웹훅 URL이 설정되지 않았습니다.");
        }
        request.setWebhookUrl(defaultWebhookUrl);
    }
}
