package com.ocp.ocp_finalproject.notice.dto.request;

import com.ocp.ocp_finalproject.notice.domain.Notice;
import com.ocp.ocp_finalproject.notice.domain.NoticeFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeFileUpdateRequest {

    private String fileName;
    private String originalName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;

    public NoticeFile toEntity(Notice notice) {
        return NoticeFile.createBuilder()
                .notice(notice)
                .fileName(fileName)
                .originalName(originalName)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .fileType(fileType)
                .build();
    }


}
