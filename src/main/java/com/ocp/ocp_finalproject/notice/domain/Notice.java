package com.ocp.ocp_finalproject.notice.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "announcement_type", length = 20)
    private String announcementType;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "notice",
            orphanRemoval = true
    )
    private List<NoticeFile> noticeFiles = new ArrayList<>();

    // 정적 팩토리 + Builder
    @Builder(builderMethodName = "createBuilder")
    public static Notice create(
            String title,
            String content,
            String announcementType,
            Boolean isImportant,
            Long authorId,
            Integer viewCount,
            String attachmentUrl,
            List<NoticeFile> noticeFiles
    ) {
        Notice notice = new Notice();
        notice.title = title;
        notice.content = content;
        notice.announcementType = announcementType;
        notice.isImportant = isImportant;
        notice.authorId = authorId;
        notice.viewCount = viewCount;
        notice.attachmentUrl = attachmentUrl;
        if (noticeFiles != null && !noticeFiles.isEmpty()) {
            // 편의 메서드 사용해 양방향 관계 세팅
            noticeFiles.forEach(notice::addNoticeFile);
        }
        return notice;
    }

    // 편의 메서드
    public void addNoticeFile(NoticeFile file) {
        this.noticeFiles.add(file);
        file.setNotice(this);
    }
    // 공지 기본 정보 수정(null 값은 기존 값 유지)
    public void update(String title, String content, String announcementType, Boolean isImportant) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (announcementType != null) this.announcementType = announcementType;
        if (isImportant != null) this.isImportant = isImportant;
    }
}
