package com.ocp.ocp_finalproject.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 공통 에러 (COMMON) =====
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "지원하지 않는 HTTP 메서드입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_003", "잘못된 타입입니다"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_004", "필수 파라미터가 누락되었습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 내부 오류가 발생했습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_006", "접근 권한이 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_007", "인증이 필요합니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_008", "요청한 리소스를 찾을 수 없습니다"),

    // ===== 사용자 관련 (USER) =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 사용자입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_003", "이미 사용 중인 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_004", "비밀번호가 일치하지 않습니다"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "USER_005", "계정이 잠겨있습니다"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "USER_006", "비활성화된 계정입니다"),
    USER_WITHDRAWAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER_007", "회원 탈퇴 처리에 실패했습니다"),

    // ===== 계정 정지 관련 (SUSPENSION) =====
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "SUSPENSION_001", "정지된 계정입니다"),
    SUSPENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUSPENSION_002", "정지 기록을 찾을 수 없습니다"),
    ALREADY_SUSPENDED(HttpStatus.BAD_REQUEST, "SUSPENSION_003", "이미 정지된 계정입니다"),
    NOT_SUSPENDED(HttpStatus.BAD_REQUEST, "SUSPENSION_004", "정지되지 않은 계정입니다"),

    // ===== 인증/인가 관련 (AUTH) =====
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "만료된 토큰입니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_003", "리프레시 토큰을 찾을 수 없습니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "유효하지 않은 리프레시 토큰입니다"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_005", "로그인에 실패했습니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_006", "이메일 또는 비밀번호가 일치하지 않습니다"),

    // ===== 워크플로우 관련 (WORKFLOW) =====
    WORKFLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKFLOW_001", "워크플로우를 찾을 수 없습니다"),
    WORKFLOW_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "WORKFLOW_002", "이미 활성화된 워크플로우입니다"),
    WORKFLOW_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "WORKFLOW_003", "비활성화된 워크플로우입니다"),
    NOT_WORKFLOW_OWNER(HttpStatus.FORBIDDEN, "WORKFLOW_004", "워크플로우 소유자만 수정/삭제할 수 있습니다"),
    WORKFLOW_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORKFLOW_005", "워크플로우 생성에 실패했습니다"),
    WORKFLOW_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORKFLOW_006", "워크플로우 수정에 실패했습니다"),
    WORKFLOW_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORKFLOW_007", "워크플로우 삭제에 실패했습니다"),
    WORKFLOW_TEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORKFLOW_008", "워크플로우 테스트에 실패했습니다"),
    INVALID_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "WORKFLOW_009", "허용되지 않는 상태 전환입니다"),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "WORKFLOW_010", "이미 삭제된 워크플로우입니다"),
    WORKFLOW_TEST_NOT_PASSED(HttpStatus.BAD_REQUEST, "WORKFLOW_011", "워크플로우 테스트가 완료되지 않았습니다"),

    // Recurrence Rule 관련 에러
    RECURRENCE_RULE_INVALID(HttpStatus.BAD_REQUEST, "RECURRENCE_RULE_001", "잘못된 반복 규칙입니다"),
    RECURRENCE_RULE_REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "RECURRENCE_RULE_002", "필수 필드가 누락되었습니다"),
    RECURRENCE_RULE_INVALID_FIELD_VALUE(HttpStatus.BAD_REQUEST, "RECURRENCE_RULE_003", "필드 값이 올바르지 않습니다"),
    RECURRENCE_RULE_FIELD_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "RECURRENCE_RULE_004", "이 반복 유형에서는 사용할 수 없는 필드입니다"),
    RECURRENCE_RULE_DATE_RANGE_INVALID(HttpStatus.BAD_REQUEST, "RECURRENCE_RULE_005", "종료 일시는 시작 일시보다 이후여야 합니다"),

    // ===== 작업 관련 (WORK) =====
    WORK_NOT_FOUND(HttpStatus.NOT_FOUND, "WORK_001", "작업을 찾을 수 없습니다"),
    WORK_ALREADY_RUNNING(HttpStatus.BAD_REQUEST, "WORK_002", "이미 실행 중인 작업입니다"),
    WORK_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORK_003", "작업 실행에 실패했습니다"),
    WORK_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "WORK_004", "작업 로그를 찾을 수 없습니다"),
    WORK_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WORK_005", "작업 생성에 실패했습니다"),
    WORK_WEBHOOK_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "WORK_006", "유효하지 않은 웹훅 토큰입니다"),
    WORK_WEBHOOK_URL_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "WORK_007", "블로그 웹훅 URL이 설정되지 않았습니다"),

    // ===== 블로그 관련 (BLOG) =====
    BLOG_NOT_FOUND(HttpStatus.NOT_FOUND, "BLOG_001", "블로그를 찾을 수 없습니다"),
    BLOG_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "BLOG_002", "블로그 인증에 실패했습니다"),
    DUPLICATE_BLOG(HttpStatus.CONFLICT, "BLOG_003", "이미 등록된 블로그입니다"),
    BLOG_POST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BLOG_004", "블로그 게시 중 오류가 발생했습니다"),
    BLOG_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "BLOG_005", "블로그 타입을 찾을 수 없습니다"),
    BLOG_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BLOG_006", "블로그 정보 수정에 실패했습니다"),
    INVALID_BLOG_CREDENTIALS(HttpStatus.BAD_REQUEST, "BLOG_007", "블로그 계정 정보가 유효하지 않습니다"),

    // ===== 게시글 관련 (POST) =====
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "게시글을 찾을 수 없습니다"),
    POST_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "POST_002", "이미 삭제된 게시글입니다"),
    NOT_POST_OWNER(HttpStatus.FORBIDDEN, "POST_003", "게시글 작성자만 수정/삭제할 수 있습니다"),
    INVALID_POST_CONTENT(HttpStatus.BAD_REQUEST, "POST_004", "게시글 내용이 유효하지 않습니다"),
    POST_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POST_005", "게시글 생성에 실패했습니다"),
    POST_STATS_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_006", "게시글 통계를 찾을 수 없습니다"),

    // ===== 크롤링 관련 (CRAWL) =====
    CRAWL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL_001", "크롤링에 실패했습니다"),
    INVALID_URL(HttpStatus.BAD_REQUEST, "CRAWL_002", "유효하지 않은 URL입니다"),
    CRAWL_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "CRAWL_003", "크롤링 시간이 초과되었습니다"),
    HTML_EXTRACT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL_004", "HTML 추출에 실패했습니다"),
    HTML_NOT_FOUND(HttpStatus.NOT_FOUND, "CRAWL_005", "HTML 추출 결과를 찾을 수 없습니다"),

    // ===== 트렌드 관련 (TREND) =====
    TREND_NOT_FOUND(HttpStatus.NOT_FOUND, "TREND_001", "트렌드 키워드를 찾을 수 없습니다"),
    TREND_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TREND_002", "트렌드 조회에 실패했습니다"),
    TREND_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "TREND_003", "트렌드 카테고리를 찾을 수 없습니다"),
    TREND_KEYWORD_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TREND_004", "트렌드 키워드 추가에 실패했습니다"),

    // ===== 상품 관련 (PRODUCT) =====
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "상품을 찾을 수 없습니다"),
    PRODUCT_CRAWL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_002", "상품 크롤링에 실패했습니다"),
    PRODUCT_EXTRACT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_003", "상품 추출에 실패했습니다"),
    PRODUCT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_004", "상품 삭제에 실패했습니다"),

    // ===== AI 컨텐츠 관련 (AI_CONTENT) =====
    AI_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AI_CONTENT_001", "AI 생성 컨텐츠를 찾을 수 없습니다"),
    AI_CONTENT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_CONTENT_002", "AI 컨텐츠 생성에 실패했습니다"),
    AI_KEYWORD_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_CONTENT_003", "AI 키워드 생성에 실패했습니다"),

    // ===== AI 서비스 관련 (AI) =====
    AI_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI_001", "AI 서비스 오류가 발생했습니다"),
    AI_RESPONSE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "AI_002", "AI 응답 시간이 초과되었습니다"),
    INVALID_AI_PROMPT(HttpStatus.BAD_REQUEST, "AI_003", "유효하지 않은 AI 프롬프트입니다"),
    AI_TOKEN_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI_004", "AI 토큰 사용량을 초과했습니다"),

    // ===== 공지사항 관련 (NOTICE) =====
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE_001", "공지사항을 찾을 수 없습니다"),
    NOTICE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE_002", "공지사항 등록에 실패했습니다"),
    NOTICE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE_003", "공지사항 수정에 실패했습니다"),
    NOTICE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE_004", "공지사항 삭제에 실패했습니다"),

    // ===== 파일 관련 (FILE) =====
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_001", "파일을 찾을 수 없습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_002", "지원하지 않는 파일 형식입니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_003", "파일 크기가 초과되었습니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_004", "파일 업로드에 실패했습니다"),

    // ===== 통계 관련 (STATISTICS) =====
    STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STAT_001", "통계 데이터를 찾을 수 없습니다"),
    STATISTICS_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STAT_002", "통계 계산에 실패했습니다"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "STAT_003", "유효하지 않은 날짜 범위입니다"),
    DASHBOARD_DATA_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STAT_004", "대시보드 데이터 조회에 실패했습니다"),

    // ===== 공통 코드 관련 (COMMON_CODE) =====
    COMMON_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "CODE_001", "공통코드를 찾을 수 없습니다"),
    DUPLICATE_COMMON_CODE(HttpStatus.CONFLICT, "CODE_002", "이미 존재하는 공통코드입니다"),
    COMMON_CODE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CODE_003", "공통코드 수정에 실패했습니다"),
    COMMON_CODE_DEACTIVATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CODE_004", "공통코드 비활성화에 실패했습니다"),
    COMMON_CODE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "CODE_005", "공통코드 그룹을 찾을 수 없습니다"),

    // ===== 로그 관련 (LOG) =====
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "LOG_001", "로그를 찾을 수 없습니다"),
    ERROR_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "LOG_002", "에러 로그를 찾을 수 없습니다"),
    LOG_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LOG_003", "로그 조회에 실패했습니다"),

    // ===== 사이트 URL 관련 (SITE) =====
    SITE_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "SITE_001", "사이트 URL을 찾을 수 없습니다"),
    SITE_URL_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SITE_002", "사이트 URL 수정에 실패했습니다"),
    DUPLICATE_SITE_URL(HttpStatus.CONFLICT, "SITE_003", "이미 등록된 사이트 URL입니다"),

    // ===== 데이터베이스 관련 (DB) =====
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_001", "데이터베이스 오류가 발생했습니다"),
    DUPLICATE_KEY(HttpStatus.CONFLICT, "DB_002", "중복된 데이터입니다"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DB_003", "데이터 무결성 제약 조건 위반입니다"),
    TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB_004", "트랜잭션 처리에 실패했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
