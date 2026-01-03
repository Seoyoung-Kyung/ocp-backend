package com.ocp.ocp_finalproject.monitoring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParsedLog {
    private String timestamp;
    private String fileName;
    private Integer lineNumber;
    private String logLevel;
    private String message;
    private String rawLine; // 관리자용 원본 라인
}

