-- system_logs (시스템 로그 - 1년치 데이터)
-- log_level: INFO, WARN, ERROR
-- error_type: NETWORK_ERROR, VALIDATION_ERROR, DATABASE_ERROR, API_ERROR, TIMEOUT_ERROR, AUTH_ERROR
-- task_type: WORKFLOW_EXECUTION, BLOG_UPLOAD, CRAWLING, AI_REQUEST, USER_AUTH, SCHEDULED_TASK

-- 2024년 12월
INSERT INTO system_logs (log_level, message, error_type, task_type, ip_address, stack_trace, request_data, created_at, updated_at)
VALUES
-- 12월 24일
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '127.0.0.1', NULL, '{"workflow_id":1,"user_id":1}', '2024-12-24 09:15:20', '2024-12-24 09:15:20'),
('INFO', 'AI 키워드 선택 요청 성공', NULL, 'AI_REQUEST', '127.0.0.1', NULL, '{"feature":"KEYWORD_SELECTION","work_id":1}', '2024-12-24 09:15:23', '2024-12-24 09:15:23'),
('INFO', '워크플로우 실행 완료', NULL, 'WORKFLOW_EXECUTION', '127.0.0.1', NULL, '{"workflow_id":1,"status":"SUCCESS"}', '2024-12-24 10:30:45', '2024-12-24 10:30:45'),
('ERROR', '블로그 업로드 실패: 네트워크 오류', 'NETWORK_ERROR', 'BLOG_UPLOAD', '192.168.1.10', 'java.net.ConnectException: Connection refused\n\tat com.ocp.service.BlogUploadService.upload(BlogUploadService.java:142)', '{"blog_id":1,"platform":"TISTORY"}', '2024-12-24 11:20:35', '2024-12-24 11:20:35'),
('WARN', '크롤링 재시도 중 (1/3)', NULL, 'CRAWLING', '127.0.0.1', NULL, '{"product_id":1,"retry_count":1}', '2024-12-24 13:45:22', '2024-12-24 13:45:22'),
('INFO', '크롤링 성공', NULL, 'CRAWLING', '127.0.0.1', NULL, '{"product_id":1,"items_count":25}', '2024-12-24 13:46:10', '2024-12-24 13:46:10'),
-- 12월 25일
('INFO', '사용자 로그인', NULL, 'USER_AUTH', '192.168.1.15', NULL, '{"user_id":2,"login_method":"KAKAO"}', '2024-12-25 08:30:12', '2024-12-25 08:30:12'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.15', NULL, '{"workflow_id":2,"user_id":2}', '2024-12-25 08:45:30', '2024-12-25 08:45:30'),
('ERROR', 'API 호출 실패: 타임아웃', 'TIMEOUT_ERROR', 'AI_REQUEST', '192.168.1.15', 'java.net.SocketTimeoutException: Read timed out\n\tat com.ocp.service.AIService.callAPI(AIService.java:89)', '{"feature":"CONTENT_GENERATION","timeout":30000}', '2024-12-25 10:15:45', '2024-12-25 10:15:45'),
('WARN', 'AI 요청 재시도 (1/3)', NULL, 'AI_REQUEST', '192.168.1.15', NULL, '{"feature":"CONTENT_GENERATION","retry":1}', '2024-12-25 10:16:00', '2024-12-25 10:16:00'),
('INFO', 'AI 콘텐츠 생성 성공', NULL, 'AI_REQUEST', '192.168.1.15', NULL, '{"work_id":5,"tokens":3150}', '2024-12-25 14:18:27', '2024-12-25 14:18:27'),
-- 12월 26일
('INFO', '예약 작업 실행: 일일 통계 집계', NULL, 'SCHEDULED_TASK', '127.0.0.1', NULL, '{"task":"daily_statistics","date":"2024-12-25"}', '2024-12-26 00:00:05', '2024-12-26 00:00:05'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.20', NULL, '{"workflow_id":3,"user_id":3}', '2024-12-26 09:30:10', '2024-12-26 09:30:10'),
('INFO', '크롤링 시작', NULL, 'CRAWLING', '192.168.1.20', NULL, '{"product_id":2,"url":"https://example.com"}', '2024-12-26 09:35:22', '2024-12-26 09:35:22'),
('ERROR', '데이터베이스 오류: 중복 키', 'DATABASE_ERROR', 'BLOG_UPLOAD', '192.168.1.20', 'org.springframework.dao.DuplicateKeyException: Duplicate entry\n\tat com.ocp.repository.BlogRepository.save(BlogRepository.java:45)', '{"ai_content_id":8}', '2024-12-26 12:25:18', '2024-12-26 12:25:18'),
('INFO', '블로그 업로드 성공', NULL, 'BLOG_UPLOAD', '192.168.1.25', NULL, '{"ai_content_id":9,"platform":"NAVER","url":"https://blog.naver.com/post/123"}', '2024-12-26 15:40:32', '2024-12-26 15:40:32'),
-- 12월 27일
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.30', NULL, '{"workflow_id":4,"user_id":5}', '2024-12-27 08:20:40', '2024-12-27 08:20:40'),
('INFO', 'AI 키워드 선택 성공', NULL, 'AI_REQUEST', '192.168.1.30', NULL, '{"work_id":10,"keywords":["여행","제주도","맛집"]}', '2024-12-27 08:20:45', '2024-12-27 08:20:45'),
('WARN', '유효성 검사 경고: 제목 길이 초과', 'VALIDATION_ERROR', 'BLOG_UPLOAD', '192.168.1.30', NULL, '{"title_length":152,"max_length":150}', '2024-12-27 11:45:22', '2024-12-27 11:45:22'),
('INFO', '블로그 업로드 성공', NULL, 'BLOG_UPLOAD', '192.168.1.30', NULL, '{"ai_content_id":11,"platform":"TISTORY"}', '2024-12-27 11:50:38', '2024-12-27 11:50:38'),
-- 12월 28일
('INFO', '예약 작업 실행: 일일 통계 집계', NULL, 'SCHEDULED_TASK', '127.0.0.1', NULL, '{"task":"daily_statistics","date":"2024-12-27"}', '2024-12-28 00:00:05', '2024-12-28 00:00:05'),
('INFO', '사용자 로그인', NULL, 'USER_AUTH', '192.168.1.35', NULL, '{"user_id":7,"login_method":"NAVER"}', '2024-12-28 10:10:25', '2024-12-28 10:10:25'),
('ERROR', '인증 오류: 만료된 토큰', 'AUTH_ERROR', 'USER_AUTH', '192.168.1.40', 'com.ocp.exception.TokenExpiredException: JWT token expired\n\tat com.ocp.security.JwtFilter.validate(JwtFilter.java:67)', '{"user_id":8}', '2024-12-28 13:25:42', '2024-12-28 13:25:42'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.40', NULL, '{"workflow_id":5,"user_id":8}', '2024-12-28 13:30:20', '2024-12-28 13:30:20'),
('INFO', '이미지 생성 성공', NULL, 'AI_REQUEST', '192.168.1.40', NULL, '{"work_id":16,"image_url":"https://cdn.example.com/img123.png"}', '2024-12-28 15:45:18', '2024-12-28 15:45:18'),
-- 12월 29일
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.45', NULL, '{"workflow_id":6,"user_id":9}', '2024-12-29 09:20:30', '2024-12-29 09:20:30'),
('INFO', '크롤링 시작', NULL, 'CRAWLING', '192.168.1.45', NULL, '{"product_id":3,"platform":"COUPANG"}', '2024-12-29 10:15:22', '2024-12-29 10:15:22'),
('WARN', '크롤링 경고: 일부 데이터 누락', NULL, 'CRAWLING', '192.168.1.45', NULL, '{"missing_fields":["price","rating"],"count":3}', '2024-12-29 10:16:45', '2024-12-29 10:16:45'),
('INFO', 'AI 콘텐츠 생성 성공', NULL, 'AI_REQUEST', '192.168.1.45', NULL, '{"work_id":18,"word_count":1200}', '2024-12-29 11:35:42', '2024-12-29 11:35:42'),
-- 12월 30일
('INFO', '예약 작업 실행: 일일 통계 집계', NULL, 'SCHEDULED_TASK', '127.0.0.1', NULL, '{"task":"daily_statistics","date":"2024-12-29"}', '2024-12-30 00:00:05', '2024-12-30 00:00:05'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.50', NULL, '{"workflow_id":7,"user_id":10}', '2024-12-30 08:40:18', '2024-12-30 08:40:18'),
('INFO', 'AI 키워드 선택 성공', NULL, 'AI_REQUEST', '192.168.1.50', NULL, '{"work_id":19,"keywords":5}', '2024-12-30 08:40:22', '2024-12-30 08:40:22'),
('ERROR', 'API 호출 실패: 할당량 초과', 'API_ERROR', 'AI_REQUEST', '192.168.1.50', 'com.ocp.exception.QuotaExceededException: API quota exceeded\n\tat com.ocp.service.AIService.checkQuota(AIService.java:102)', '{"user_id":10,"daily_quota":100,"used":101}', '2024-12-30 16:20:35', '2024-12-30 16:20:35'),
('INFO', '블로그 업로드 성공', NULL, 'BLOG_UPLOAD', '192.168.1.55', NULL, '{"ai_content_id":22,"platform":"TISTORY"}', '2024-12-30 14:20:45', '2024-12-30 14:20:45'),
('INFO', '이미지 생성 성공', NULL, 'AI_REQUEST', '192.168.1.60', NULL, '{"work_id":23}', '2024-12-30 16:30:15', '2024-12-30 16:30:15'),
-- 12월 31일
('INFO', '예약 작업 실행: 일일 통계 집계', NULL, 'SCHEDULED_TASK', '127.0.0.1', NULL, '{"task":"daily_statistics","date":"2024-12-30"}', '2024-12-31 00:00:05', '2024-12-31 00:00:05'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.65', NULL, '{"workflow_id":8,"user_id":4}', '2024-12-31 09:10:38', '2024-12-31 09:10:38'),
('INFO', 'AI 콘텐츠 생성 시작', NULL, 'AI_REQUEST', '192.168.1.65', NULL, '{"work_id":24}', '2024-12-31 09:10:42', '2024-12-31 09:10:42'),
('INFO', '블로그 업로드 성공', NULL, 'BLOG_UPLOAD', '192.168.1.70', NULL, '{"ai_content_id":26,"platform":"NAVER"}', '2024-12-31 13:25:22', '2024-12-31 13:25:22'),
('INFO', '워크플로우 실행 완료', NULL, 'WORKFLOW_EXECUTION', '192.168.1.70', NULL, '{"workflow_id":8,"duration_ms":15240}', '2024-12-31 16:00:18', '2024-12-31 16:00:18');

-- 2025년 1월 (샘플 데이터)
INSERT INTO system_logs (log_level, message, error_type, task_type, ip_address, stack_trace, request_data, created_at, updated_at)
VALUES
('INFO', '예약 작업 실행: 일일 통계 집계', NULL, 'SCHEDULED_TASK', '127.0.0.1', NULL, '{"task":"daily_statistics","date":"2024-12-31"}', '2025-01-01 00:00:05', '2025-01-01 00:00:05'),
('INFO', '워크플로우 실행 시작', NULL, 'WORKFLOW_EXECUTION', '192.168.1.75', NULL, '{"workflow_id":9,"user_id":8}', '2025-01-01 09:30:10', '2025-01-01 09:30:10'),
('INFO', '사용자 로그인', NULL, 'USER_AUTH', '192.168.1.80', NULL, '{"user_id":1,"login_method":"KAKAO"}', '2025-01-02 08:15:22', '2025-01-02 08:15:22'),
('ERROR', '블로그 업로드 실패: 네트워크 오류', 'NETWORK_ERROR', 'BLOG_UPLOAD', '192.168.1.80', 'java.net.ConnectException: Connection timed out\n\tat com.ocp.service.BlogUploadService.upload(BlogUploadService.java:142)', '{"blog_id":2,"platform":"TISTORY"}', '2025-01-02 11:40:35', '2025-01-02 11:40:35'),
('WARN', '블로그 업로드 재시도 (1/3)', NULL, 'BLOG_UPLOAD', '192.168.1.80', NULL, '{"ai_content_id":32}', '2025-01-02 11:41:00', '2025-01-02 11:41:00'),
('INFO', '블로그 업로드 성공', NULL, 'BLOG_UPLOAD', '192.168.1.80', NULL, '{"ai_content_id":32,"platform":"TISTORY"}', '2025-01-02 11:41:35', '2025-01-02 11:41:35'),
('INFO', '이미지 생성 성공', NULL, 'AI_REQUEST', '192.168.1.85', NULL, '{"work_id":33}', '2025-01-02 15:25:18', '2025-01-02 15:25:18'),
('INFO', 'AI 키워드 선택 성공', NULL, 'AI_REQUEST', '192.168.1.90', NULL, '{"work_id":34}', '2025-01-03 09:15:33', '2025-01-03 09:15:33'),
('INFO', '워크플로우 실행 완료', NULL, 'WORKFLOW_EXECUTION', '192.168.1.90', NULL, '{"workflow_id":10,"status":"SUCCESS"}', '2025-01-03 14:30:45', '2025-01-03 14:30:45'),
('ERROR', '데이터베이스 연결 실패', 'DATABASE_ERROR', 'WORKFLOW_EXECUTION', '192.168.1.95', 'java.sql.SQLException: Connection refused\n\tat com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:123)', '{"workflow_id":11}', '2025-01-04 10:25:42', '2025-01-04 10:25:42');

-- 2025년 2월 ~ 12월 (대량 샘플 데이터 생성)
INSERT INTO system_logs (log_level, message, error_type, task_type, ip_address, stack_trace, request_data, created_at, updated_at)
SELECT
    CASE (FLOOR(RAND() * 10))
        WHEN 0 THEN 'ERROR'
        WHEN 1 THEN 'WARN'
        ELSE 'INFO'
    END as log_level,
    CASE (FLOOR(RAND() * 15))
        WHEN 0 THEN '워크플로우 실행 시작'
        WHEN 1 THEN '워크플로우 실행 완료'
        WHEN 2 THEN 'AI 요청 성공'
        WHEN 3 THEN '블로그 업로드 성공'
        WHEN 4 THEN '크롤링 시작'
        WHEN 5 THEN '크롤링 완료'
        WHEN 6 THEN '사용자 로그인'
        WHEN 7 THEN '예약 작업 실행'
        WHEN 8 THEN '블로그 업로드 실패'
        WHEN 9 THEN 'API 호출 실패'
        WHEN 10 THEN '데이터베이스 오류'
        WHEN 11 THEN '네트워크 타임아웃'
        WHEN 12 THEN '유효성 검사 실패'
        WHEN 13 THEN 'AI 요청 재시도'
        ELSE '시스템 정상 작동'
    END as message,
    CASE
        WHEN FLOOR(RAND() * 10) = 0 THEN
            CASE (FLOOR(RAND() * 6))
                WHEN 0 THEN 'NETWORK_ERROR'
                WHEN 1 THEN 'VALIDATION_ERROR'
                WHEN 2 THEN 'DATABASE_ERROR'
                WHEN 3 THEN 'API_ERROR'
                WHEN 4 THEN 'TIMEOUT_ERROR'
                ELSE 'AUTH_ERROR'
            END
        ELSE NULL
    END as error_type,
    CASE (FLOOR(RAND() * 6))
        WHEN 0 THEN 'WORKFLOW_EXECUTION'
        WHEN 1 THEN 'BLOG_UPLOAD'
        WHEN 2 THEN 'CRAWLING'
        WHEN 3 THEN 'AI_REQUEST'
        WHEN 4 THEN 'USER_AUTH'
        ELSE 'SCHEDULED_TASK'
    END as task_type,
    CONCAT('192.168.1.', FLOOR(1 + RAND() * 254)) as ip_address,
    CASE
        WHEN FLOOR(RAND() * 10) = 0 THEN 'java.lang.Exception: Error occurred\n\tat com.ocp.service.SomeService.method(SomeService.java:100)'
        ELSE NULL
    END as stack_trace,
    CONCAT('{"id":', FLOOR(1 + RAND() * 100), ',"timestamp":"', NOW(), '"}') as request_data,
    DATE_ADD('2025-01-05', INTERVAL seq * 2 HOUR) as created_at,
    DATE_ADD('2025-01-05', INTERVAL seq * 2 HOUR) as updated_at
FROM (
    SELECT @row := @row + 1 as seq
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t3,
         (SELECT @row := 0) r
    WHERE @row < 2000
) nums
WHERE DATE_ADD('2025-01-05', INTERVAL seq * 2 HOUR) <= '2025-12-23 23:59:59';