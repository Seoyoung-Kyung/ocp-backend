package com.ocp.ocp_finalproject.notice.dto.request;

import com.ocp.ocp_finalproject.notice.domain.Notice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeCreateRequest {

    private String title;
    private String content;

    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;

    private String announcementType;
    private Boolean isImportant;

    public Notice toEntity(Long authorId) {
        return Notice.createBuilder()
                .title(title)
                .content(content)
                .announcementType(announcementType)
                .isImportant(isImportant)
                .authorId(authorId)
                .viewCount(0)
                .attachmentUrl(null)
                .noticeFiles(null)
                .build();
    }
}
