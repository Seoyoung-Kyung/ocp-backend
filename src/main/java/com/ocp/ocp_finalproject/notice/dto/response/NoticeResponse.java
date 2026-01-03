package com.ocp.ocp_finalproject.notice.dto.response;

import com.ocp.ocp_finalproject.notice.domain.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {
    private Long noticeId;
    private String title;
    private String content;
    private String announcementType;
    private Boolean isImportant;
    private String userName;
    private Integer viewCount;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private NoticeFileResponse noticeFile;

    public static NoticeResponse of(Notice notice, String authorName) {
        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .announcementType(notice.getAnnouncementType())
                .isImportant(notice.getIsImportant())
                .userName(authorName)
                .viewCount(notice.getViewCount())
                .attachmentUrl(notice.getAttachmentUrl())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .noticeFile(
                        notice.getNoticeFiles().isEmpty() ?
                                null :
                                NoticeFileResponse.from(notice.getNoticeFiles().get(0))
                )
                .build();
    }
}
