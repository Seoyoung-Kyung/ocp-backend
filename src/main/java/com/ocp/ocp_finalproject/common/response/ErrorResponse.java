package com.ocp.ocp_finalproject.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    // success: 항상 false (에러 응답이니까)
    private boolean success;

    // errorCode: 에러 코드 (예: "USER_NOT_FOUND", "INVALID_INPUT")
    private String errorCode;

    // message: 에러 메시지 (예: "사용자를 찾을 수 없습니다")
    private String message;

    //timestamp: 에러 발생 시간
    private LocalDateTime timestamp;

    //에러 응답 생성 (정적 팩토리 메서드)
    public static ErrorResponse error(String errorCode, String message) {
        return new ErrorResponse(false, errorCode, message, LocalDateTime.now());
    }
}
