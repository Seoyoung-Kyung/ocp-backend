package com.ocp.ocp_finalproject.monitoring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDetailLogResponse {
    private String stepName;      // "상품 크롤링"
    private List<String> messages;            // "카테고리 '1차 카테고리' 선택 → 패션의류"
    private String status;         // "성공"
    private LocalDateTime timestamp;
}
