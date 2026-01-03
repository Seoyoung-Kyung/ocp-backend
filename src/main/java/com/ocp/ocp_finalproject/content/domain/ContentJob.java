package com.ocp.ocp_finalproject.content.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "content_job")
public class ContentJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_job_id")
    private Long id;

    @Column(name = "job_id", nullable = false, unique = true, length = 36)
    private String jobId;

    @Column(name = "task_type", nullable = false)
    private String taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContentJobStatus status;

    @Builder
    private ContentJob(String jobId, String taskType, ContentJobStatus status) {
        this.jobId = jobId;
        this.taskType = taskType;
        this.status = status;
    }

    public void updateStatus(ContentJobStatus status) {
        this.status = status;
    }

    public static ContentJob pending(String jobId, String taskType) {
        return ContentJob.builder()
                .jobId(jobId)
                .taskType(taskType)
                .status(ContentJobStatus.PENDING)
                .build();
    }
}
