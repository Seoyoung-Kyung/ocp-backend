package com.ocp.ocp_finalproject.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequest {
    private String title;
    private String content;
    private String announcementType;
    private Boolean isImportant;

    // 단일 파일 업데이트
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
}
