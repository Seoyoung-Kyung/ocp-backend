-- 16. 공통 코드 그룹 (common_code_group)
-- ============================================
INSERT INTO `common_code_group` (`group_id`, `group_name`, `description`, `created_at`, `updated_at`)
VALUES ('EXECUTION_STATUS', '실행 상태', '실행 상태', NOW(), NOW()),
       ('CONTENT_STATUS', 'AI 콘텐츠 생성 상태', 'AI 콘텐츠 생성 상태', NOW(), NOW()),
       ('POST_STATUS', '블로그 포스트 발행 상태', '블로그 포스트 발행 상태', NOW(), NOW()),
       ('AUTH_PROVIDER', '인증 제공자', '인증 제공자', NOW(), NOW()),
       ('USER_ROLE', '사용자 권한', '사용자 권한', NOW(), NOW()),
       ('USER_STATUS', '사용자 계정 상태', '사용자 계정 상태', NOW(), NOW()),
       ('REPEAT_TYPE', '반복 유형', '반복 유형', NOW(), NOW()),
       ('WORKFLOW_STATUS', '워크플로우 상태', '워크플로우 상태', NOW(), NOW()),
       ('LOG_LEVEL', '로그 레벨', '로그 레벨', NOW(), NOW()),
       ('STEP_STATUS', '단계 실행 상태', '단계 실행 상태', NOW(), NOW()),
       ('SITE_URL_INFO', '쇼핑몰 사이트 정보', '쇼핑몰 사이트 정보', NOW(), NOW());

-- ============================================
-- 17. 공통 코드 (common_code)
-- ============================================
INSERT INTO `common_code` (`code_id`, `group_id`, `code_name`, `description`, `sort_order`, `is_active`, `created_at`,
                           `updated_at`)
VALUES
-- EXECUTION_STATUS
('PENDING', 'EXECUTION_STATUS', '대기', '실행 대기 중', 1, TRUE, NOW(), NOW()),
('RUNNING', 'EXECUTION_STATUS', '실행중', '실행 중', 2, TRUE, NOW(), NOW()),
('COMPLETED', 'EXECUTION_STATUS', '완료', '실행 완료', 3, TRUE, NOW(), NOW()),
('FAILED', 'EXECUTION_STATUS', '실패', '실행 실패', 4, TRUE, NOW(), NOW()),
-- CONTENT_STATUS
('CONTENT_PENDING', 'CONTENT_STATUS', '대기', 'AI 생성 대기 중', 1, TRUE, NOW(), NOW()),
('GENERATING', 'CONTENT_STATUS', '생성중', 'AI가 콘텐츠 생성 중', 2, TRUE, NOW(), NOW()),
('GENERATED', 'CONTENT_STATUS', '생성완료', 'AI 콘텐츠 생성 완료', 3, TRUE, NOW(), NOW()),
('APPROVED', 'CONTENT_STATUS', '승인', '사용자가 콘텐츠 승인', 4, TRUE, NOW(), NOW()),
('REJECTED', 'CONTENT_STATUS', '거부', '사용자가 콘텐츠 거부', 5, TRUE, NOW(), NOW()),
('PUBLISHED', 'CONTENT_STATUS', '발행완료', '블로그에 발행 완료', 6, TRUE, NOW(), NOW()),
('CONTENT_FAILED', 'CONTENT_STATUS', '실패', 'AI 콘텐츠 생성 실패', 7, TRUE, NOW(), NOW()),
-- POST_STATUS
('SCHEDULED', 'POST_STATUS', '예약', '발행 예약됨', 1, TRUE, NOW(), NOW()),
('PUBLISHING', 'POST_STATUS', '발행중', '블로그에 발행 중', 2, TRUE, NOW(), NOW()),
('POST_PUBLISHED', 'POST_STATUS', '발행완료', '블로그에 발행 완료', 3, TRUE, NOW(), NOW()),
('POST_FAILED', 'POST_STATUS', '실패', '발행 실패', 4, TRUE, NOW(), NOW()),
-- AUTH_PROVIDER
('LOCAL', 'AUTH_PROVIDER', '로컬', '이메일/비밀번호 로그인', 1, TRUE, NOW(), NOW()),
('KAKAO', 'AUTH_PROVIDER', '카카오', '카카오 계정 로그인', 2, TRUE, NOW(), NOW()),
('GOOGLE', 'AUTH_PROVIDER', '구글', '구글 계정 로그인', 3, TRUE, NOW(), NOW()),
('NAVER', 'AUTH_PROVIDER', '네이버', '네이버 계정 로그인', 4, TRUE, NOW(), NOW()),
-- USER_ROLE
('ADMIN', 'USER_ROLE', '관리자', '시스템 전체 관리 권한', 1, TRUE, NOW(), NOW()),
('USER', 'USER_ROLE', '사용자', '일반 사용자 권한', 2, TRUE, NOW(), NOW()),
('GUEST', 'USER_ROLE', '게스트', '제한된 권한', 3, TRUE, NOW(), NOW()),
-- USER_STATUS
('ACTIVE', 'USER_STATUS', '활성', '정상 활동 중인 사용자', 1, TRUE, NOW(), NOW()),
('INACTIVE', 'USER_STATUS', '비활성', '로그인하지 않은 상태', 2, TRUE, NOW(), NOW()),
('SUSPENDED', 'USER_STATUS', '정지', '관리자에 의해 정지됨', 3, TRUE, NOW(), NOW()),
('DORMANT', 'USER_STATUS', '휴면', '장기간 미접속으로 휴면 전환', 4, TRUE, NOW(), NOW()),
('WITHDRAWN', 'USER_STATUS', '탈퇴', '회원 탈퇴', 5, TRUE, NOW(), NOW()),
-- REPEAT_TYPE
('ONCE', 'REPEAT_TYPE', '한번만', '1회만 실행', 1, TRUE, NOW(), NOW()),
('DAILY', 'REPEAT_TYPE', '매일', '매일 반복 실행', 2, TRUE, NOW(), NOW()),
('WEEKLY', 'REPEAT_TYPE', '매주', '매주 반복 실행', 3, TRUE, NOW(), NOW()),
('MONTHLY', 'REPEAT_TYPE', '매월', '매월 반복 실행', 4, TRUE, NOW(), NOW()),
('CUSTOM', 'REPEAT_TYPE', '사용자정의', '사용자가 정의한 반복 규칙', 5, TRUE, NOW(), NOW()),
-- WORKFLOW_STATUS
('WORKFLOW_PENDING', 'WORKFLOW_STATUS', '대기', '워크플로우 생성 후 첫 실행 대기 중', 1, TRUE, NOW(), NOW()),
('WORKFLOW_ACTIVE', 'WORKFLOW_STATUS', '활성', '워크플로우가 활성화되어 자동 실행 중', 2, TRUE, NOW(), NOW()),
('WORKFLOW_INACTIVE', 'WORKFLOW_STATUS', '비활성', '워크플로우가 비활성화되어 실행 중지', 3, TRUE, NOW(), NOW()),
('WORKFLOW_COMPLETED', 'WORKFLOW_STATUS', '완료', '워크플로우 완전 종료', 4, TRUE, NOW(), NOW()),
-- LOG_LEVEL
('TRACE', 'LOG_LEVEL', '추적', '가장 상세한 추적 정보', 1, TRUE, NOW(), NOW()),
('DEBUG', 'LOG_LEVEL', '디버그', '디버깅용 상세 정보', 2, TRUE, NOW(), NOW()),
('INFO', 'LOG_LEVEL', '정보', '일반 정보성 메시지', 3, TRUE, NOW(), NOW()),
('WARN', 'LOG_LEVEL', '경고', '경고 메시지', 4, TRUE, NOW(), NOW()),
('ERROR', 'LOG_LEVEL', '오류', '오류 메시지', 5, TRUE, NOW(), NOW()),
('FATAL', 'LOG_LEVEL', '치명적', '치명적인 오류', 6, TRUE, NOW(), NOW()),
-- STEP_STATUS
('SUCCESS', 'STEP_STATUS', '성공', '단계 실행 성공', 1, TRUE, NOW(), NOW()),
('STEP_FAILED', 'STEP_STATUS', '실패', '단계 실행 실패', 2, TRUE, NOW(), NOW()),
('SKIPPED', 'STEP_STATUS', '건너뜀', '단계 건너뜀', 3, TRUE, NOW(), NOW()),
('RETRY', 'STEP_STATUS', '재시도', '재시도 중', 4, TRUE, NOW(), NOW()),
-- SITE_URL_INFO
('MUSINSA', 'SITE_URL_INFO', '무신사', 'https://www.musinsa.com', 1, TRUE, NOW(), NOW()),
('GMARKET', 'SITE_URL_INFO', 'G마켓', 'https://www.gmarket.co.kr', 2, TRUE, NOW(), NOW()),
('TWENTY_NINE_CM', 'SITE_URL_INFO', '29CM', 'https://www.29cm.co.kr', 3, TRUE, NOW(), NOW()),
('OLIVEYOUNG', 'SITE_URL_INFO', '올리브영', 'https://www.oliveyoung.co.kr', 4, TRUE, NOW(), NOW()),
('ELEVEN_ST', 'SITE_URL_INFO', '11번가', 'https://www.11st.co.kr', 5, TRUE, NOW(), NOW()),
('SSG', 'SITE_URL_INFO', 'SSG', 'https://www.ssg.com', 6, TRUE, NOW(), NOW()),
('EQL', 'SITE_URL_INFO', 'EQL', 'https://www.eql.kr', 7, TRUE, NOW(), NOW()),
('KREAM', 'SITE_URL_INFO', '크림', 'https://kream.co.kr', 8, TRUE, NOW(), NOW()),
('ZIGZAG', 'SITE_URL_INFO', '지그재그', 'https://www.zigzag.kr', 9, TRUE, NOW(), NOW()),
('SSADAGU', 'SITE_URL_INFO', '싸다구', 'https://www.sadagu.kr', 10, TRUE, NOW(), NOW());

-- ============================================
