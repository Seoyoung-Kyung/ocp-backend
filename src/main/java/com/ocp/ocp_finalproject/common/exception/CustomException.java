package com.ocp.ocp_finalproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Getter: errorCode 필드의 getter 자동 생성
@Getter
public class CustomException extends RuntimeException {

    // errorCode: 발생한 에러의 ErrorCode enum 값
    private final ErrorCode errorCode;

    // 생성자: ErrorCode를 받아서 CustomException 생성
    public CustomException(ErrorCode errorCode) {
        // RuntimeException의 생성자에 에러 메시지 전달
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 생성자 오버로딩: ErrorCode + 커스텀 메시지
    public CustomException(ErrorCode errorCode, String message) {
        // 커스텀 메시지로 예외 생성 (ErrorCode의 기본 메시지 대신)
        super(message);
        this.errorCode = errorCode;
    }

    // HTTP 상태 코드 반환 (GlobalExceptionHandler에서 사용)
    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    // 에러 코드 문자열 반환
    public String getCode() {
        return errorCode.getCode();
    }
}