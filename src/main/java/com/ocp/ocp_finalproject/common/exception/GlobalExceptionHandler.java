package com.ocp.ocp_finalproject.common.exception;

import com.ocp.ocp_finalproject.common.response.ErrorResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Slf4j: 로그 출력용 (log.error, log.info 등 사용 가능)
@Slf4j
// RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 잡아서 처리
// @ControllerAdvice + @ResponseBody 합친 것
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== CustomException 처리 =====
    // 우리가 직접 throw한 CustomException을 잡아서 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleCustomException(CustomException e) {
        // 에러 로그 출력
        log.error("CustomException: code={}, message={}", e.getCode(), e.getMessage());

        // ErrorResponse 생성
        ErrorResponse response = ErrorResponse.error(e.getCode(), e.getMessage());

        // HTTP 상태 코드와 함께 ErrorResponse 반환
        return ResponseEntity
                .status(e.getStatus())
                .body(response);
    }

    // ===== Validation 예외 처리 (@Valid 검증 실패) =====
    // @RequestBody에 @Valid 사용 시 검증 실패하면 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        // 첫 번째 에러 메시지 가져오기
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.INVALID_INPUT_VALUE.getCode(),
                errorMessage
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(response);
    }

    // ===== Validation 예외 처리 (Binding 실패) =====
    // ModelAttribute 바인딩 실패 시 발생
    @ExceptionHandler(BindException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage());

        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.INVALID_INPUT_VALUE.getCode(),
                errorMessage
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(response);
    }

    // ===== 타입 불일치 예외 처리 =====
    // @PathVariable, @RequestParam 타입이 안 맞을 때 발생
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.INVALID_TYPE_VALUE.getCode(),
                ErrorCode.INVALID_TYPE_VALUE.getMessage()
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_TYPE_VALUE.getStatus())
                .body(response);
    }

    // ===== 필수 파라미터 누락 예외 처리 =====
    // @RequestParam(required=true) 파라미터가 없을 때 발생
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.MISSING_REQUEST_PARAMETER.getCode(),
                ErrorCode.MISSING_REQUEST_PARAMETER.getMessage()
        );

        return ResponseEntity
                .status(ErrorCode.MISSING_REQUEST_PARAMETER.getStatus())
                .body(response);
    }

    // ===== HTTP 메서드 불일치 예외 처리 =====
    // POST 요청인데 GET만 허용하는 API 호출 시 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage()
        );

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(response);
    }

    // ===== 인증/인가 예외 처리 =====
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<@NonNull ErrorResponse> handleAccessDenied(Exception e) {
        log.error("AccessDeniedException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.error(
                ErrorCode.ACCESS_DENIED.getCode(),
                ErrorCode.ACCESS_DENIED.getMessage()
        );
        return ResponseEntity
                .status(ErrorCode.ACCESS_DENIED.getStatus())
                .body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.error(
                ErrorCode.UNAUTHORIZED.getCode(),
                ErrorCode.UNAUTHORIZED.getMessage()
        );
        return ResponseEntity
                .status(ErrorCode.UNAUTHORIZED.getStatus())
                .body(response);
    }

    // ===== 그 외 모든 예외 처리 =====
    // 위에서 처리하지 못한 모든 예외를 잡아서 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorResponse> handleException(Exception e) {
        // 예상치 못한 에러는 스택 트레이스까지 로그에 출력
        log.error("Unexpected Exception: ", e);

        ErrorResponse response = ErrorResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }
}
