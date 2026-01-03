package com.ocp.ocp_finalproject.notice.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Table(name = "notice_file")
public class NoticeFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    // 정적 팩토리 + Builder
    @Builder(builderMethodName = "createBuilder")
    public static NoticeFile create(
            Notice notice,
            String fileName,
            String originalName,
            String fileUrl,
            Long fileSize,
            String fileType
    ) {
        NoticeFile noticeFile = new NoticeFile();
        if (notice != null) {
            notice.addNoticeFile(noticeFile);  // 양방향 안전하게 연결
        }
        noticeFile.fileName = fileName;
        noticeFile.originalName = originalName;
        noticeFile.fileUrl = fileUrl;
        noticeFile.fileSize = fileSize;
        noticeFile.fileType = fileType;
        return noticeFile;
    }

    // 편의 메서드
    public void setNotice(Notice notice) {
        this.notice = notice;
        if (!notice.getNoticeFiles().contains(this)) {
            notice.getNoticeFiles().add(this);
        }
    }
}
