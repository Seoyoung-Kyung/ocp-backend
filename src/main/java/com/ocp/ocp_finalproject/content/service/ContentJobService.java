package com.ocp.ocp_finalproject.content.service;

import com.ocp.ocp_finalproject.content.domain.ContentJob;
import com.ocp.ocp_finalproject.content.domain.ContentJobStatus;
import com.ocp.ocp_finalproject.content.dto.ContentRequestDto;
import com.ocp.ocp_finalproject.content.repository.ContentJobRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentJobService {

    private final ContentJobRepository contentJobRepository;

    @Transactional
    public ContentJob createJob(ContentRequestDto requestDto) {
        String jobId = UUID.randomUUID().toString();
        ContentJob job = ContentJob.pending(jobId, requestDto.getTaskType());
        return contentJobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public Optional<ContentJob> getJob(String jobId) {
        return contentJobRepository.findByJobId(jobId);
    }

    // 상태 업데이트 로직은 추후 Worker 응답 처리 단계에서 확장 예정
    @Transactional
    public ContentJob updateStatus(String jobId, ContentJobStatus status) {
        ContentJob job = contentJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        job.updateStatus(status);
        return job;
    }
}
