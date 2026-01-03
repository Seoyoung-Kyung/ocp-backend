package com.ocp.ocp_finalproject.content.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.content.enums.ContentStatus;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "ai_content")
public class AiContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_content_id")
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "choice_product")
    private String choiceProduct;

    @Column(name = "choice_trend_keyword")
    private String choiceTrendKeyword;

    @Enumerated(EnumType.STRING)     // ★ 필수
    private ContentStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 편의 메서드
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Builder(builderMethodName = "createBuilder")
    public static AiContent create(
            String title,
            String content,
            String choiceProduct,
            String choiceTrendKeyword,
            ContentStatus status,      // ★ 누락된 필드 추가
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Work work
    ) {
        AiContent aiContent = new AiContent();
        aiContent.title = title;
        aiContent.summary = null;
        aiContent.content = content;
        aiContent.choiceProduct = choiceProduct;
        aiContent.choiceTrendKeyword = choiceTrendKeyword;
        aiContent.status = status;
        aiContent.startedAt = startedAt;
        aiContent.completedAt = completedAt;
        aiContent.setWork(work);
        return aiContent;
    }

    public void updateKeywordCompletion(boolean isSuccess, String keyword, LocalDateTime startedAt, LocalDateTime completedAt) {
        if(isSuccess) {
            this.choiceTrendKeyword=keyword;
            this.status = ContentStatus.GENERATING;
        } else{
            this.status = ContentStatus.FAILED;
        }
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    //DB에 ai_content.summary 컬럼 추가 (예: ALTER TABLE ai_content ADD COLUMN summary longtext NULL;).
    public void updateProductSelection(boolean isSuccess, String productName, LocalDateTime completedAt) {
        if (isSuccess) {
            this.choiceProduct = productName;
        } else {
            this.status = ContentStatus.FAILED;
        }
        this.completedAt = completedAt;
    }

    public void updateContentGeneration(boolean isSuccess, String title, String summary, String content, LocalDateTime completedAt) {
        if (isSuccess) {
            this.title = title;
            this.summary = summary;
            this.content = content;
            this.status = ContentStatus.GENERATED;
        } else {
            this.status = ContentStatus.FAILED;
        }
        this.completedAt = completedAt;
    }

    public void updateBlogUploadResult(boolean isSuccess, LocalDateTime completedAt) {
        this.status = isSuccess ? ContentStatus.PUBLISHED : ContentStatus.FAILED;
        this.completedAt = completedAt;
    }
}
