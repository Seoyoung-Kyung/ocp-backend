-- workflow (워크플로우 - ai_content와 연동)
-- status: PRE_REGISTERED, PENDING, ACTIVE, INACTIVE, COMPLETED, DELETED
-- test_status: NOT_TESTED, TESTING, TEST_PASSED, TEST_FAILED
-- NULL 허용 컬럼: user_blog_id, trend_category_id, recurrence_rule_id는 NULL 가능

-- 2024년 12월 ~ 2025년 1월 (ai_content 1~40번에 대응)
-- user_blog_id: site_url과 일치하는 user_blog 참조 (중복 없이 고유 할당)
-- recurrence_rule_id: 1~40 순차 할당 (OneToOne 관계로 고유해야 함)
INSERT INTO workflow (user_id, user_blog_id, trend_category_id, recurrence_rule_id, status, site_url, test_status, created_at, updated_at)
VALUES
-- 12월 24일 (ai_content 1~3)
(1, 1, 1, 1, 'ACTIVE', 'https://blog.naver.com/user1_naver', 'TEST_PASSED', '2024-12-24 08:00:00', '2024-12-24 10:30:45'),
(1, 2, 167, 2, 'ACTIVE', 'https://user1.tistory.com', 'TEST_PASSED', '2024-12-24 08:30:00', '2024-12-24 11:35:20'),
(2, 6, 168, 3, 'ACTIVE', 'https://user2.tistory.com', 'TEST_PASSED', '2024-12-24 09:00:00', '2024-12-24 11:35:20'),

-- 12월 25일 (ai_content 4~5)
(2, 7, 169, 4, 'ACTIVE', 'https://blog.naver.com/user2_naver', 'TEST_PASSED', '2024-12-25 07:30:00', '2024-12-25 14:25:40'),
(3, 11, 170, 5, 'ACTIVE', 'https://blog.naver.com/user3_naver', 'TEST_PASSED', '2024-12-25 08:00:00', '2024-12-25 14:25:40'),

-- 12월 26일 (ai_content 6~9)
(3, 12, 1, 6, 'ACTIVE', 'https://user3.tistory.com', 'TEST_PASSED', '2024-12-26 08:00:00', '2024-12-26 15:40:32'),
(4, 16, 166, 7, 'ACTIVE', 'https://user4.tistory.com', 'TEST_PASSED', '2024-12-26 08:30:00', '2024-12-26 15:40:32'),
(4, 17, 171, 8, 'INACTIVE', 'https://blog.naver.com/user4_naver', 'TEST_FAILED', '2024-12-26 09:00:00', '2024-12-26 12:25:18'),
(5, 21, 172, 9, 'ACTIVE', 'https://blog.naver.com/user5_naver', 'TEST_PASSED', '2024-12-26 09:30:00', '2024-12-26 15:40:32'),

-- 12월 27일 (ai_content 10~13)
(5, 22, 173, 10, 'ACTIVE', 'https://user5.tistory.com', 'TEST_PASSED', '2024-12-27 07:00:00', '2024-12-27 14:30:25'),
(6, 26, 2, 11, 'ACTIVE', 'https://user6.tistory.com', 'TEST_PASSED', '2024-12-27 07:30:00', '2024-12-27 14:30:25'),
(6, 27, 167, 12, 'ACTIVE', 'https://blog.naver.com/user6_naver', 'TEST_PASSED', '2024-12-27 08:00:00', '2024-12-27 14:30:25'),
(7, 31, 168, 13, 'ACTIVE', 'https://blog.naver.com/user7_naver', 'TEST_PASSED', '2024-12-27 08:30:00', '2024-12-27 14:30:25'),

-- 12월 28일 (ai_content 14~16)
(7, 32, 169, 14, 'ACTIVE', 'https://user7.tistory.com', 'TEST_PASSED', '2024-12-28 08:00:00', '2024-12-28 15:50:22'),
(8, 36, 170, 15, 'ACTIVE', 'https://user8.tistory.com', 'TEST_PASSED', '2024-12-28 08:30:00', '2024-12-28 15:50:22'),
(8, 37, 1, 16, 'ACTIVE', 'https://blog.naver.com/user8_naver', 'TEST_PASSED', '2024-12-28 09:00:00', '2024-12-28 15:50:22'),

-- 12월 29일 (ai_content 17~18)
(9, 41, 166, 17, 'ACTIVE', 'https://blog.naver.com/user9_naver', 'TEST_PASSED', '2024-12-29 08:00:00', '2024-12-29 11:40:55'),
(9, 42, 171, 18, 'ACTIVE', 'https://user9.tistory.com', 'TEST_PASSED', '2024-12-29 08:30:00', '2024-12-29 11:40:55'),

-- 12월 30일 (ai_content 19~23)
(10, 46, 172, 19, 'ACTIVE', 'https://user10.tistory.com', 'TEST_PASSED', '2024-12-30 07:00:00', '2024-12-30 16:35:28'),
(10, 47, 173, 20, 'ACTIVE', 'https://blog.naver.com/user10_naver', 'TEST_PASSED', '2024-12-30 07:30:00', '2024-12-30 16:35:28'),
(1, 3, 2, 21, 'ACTIVE', 'https://blog.naver.com/user1_fashion', 'TEST_PASSED', '2024-12-30 08:00:00', '2024-12-30 16:35:28'),
(2, 8, 167, 22, 'ACTIVE', 'https://user2-beauty.tistory.com', 'TEST_PASSED', '2024-12-30 08:30:00', '2024-12-30 16:35:28'),
(3, 13, 168, 23, 'ACTIVE', 'https://blog.naver.com/user3_game', 'TEST_PASSED', '2024-12-30 09:00:00', '2024-12-30 16:35:28'),

-- 12월 31일 (ai_content 24~27)
(4, 18, 169, 24, 'ACTIVE', 'https://user4-sports.tistory.com', 'TEST_PASSED', '2024-12-31 07:30:00', '2024-12-31 16:00:35'),
(5, 23, 170, 25, 'ACTIVE', 'https://blog.naver.com/user5_book', 'TEST_PASSED', '2024-12-31 08:00:00', '2024-12-31 16:00:35'),
(6, 28, 1, 26, 'ACTIVE', 'https://user6-interior.tistory.com', 'TEST_PASSED', '2024-12-31 08:30:00', '2024-12-31 16:00:35'),
(7, 33, 166, 27, 'ACTIVE', 'https://blog.naver.com/user7_auto', 'TEST_PASSED', '2024-12-31 09:00:00', '2024-12-31 16:00:35'),

-- 2025년 1월 (ai_content 28~40)
(8, 38, 171, 28, 'ACTIVE', 'https://user8-parent.tistory.com', 'TEST_PASSED', '2025-01-01 08:00:00', '2025-01-01 11:50:35'),
(9, 43, 172, 29, 'ACTIVE', 'https://blog.naver.com/user9_vintage', 'TEST_PASSED', '2025-01-01 08:30:00', '2025-01-01 11:50:35'),
(10, 48, 173, 30, 'ACTIVE', 'https://user10-craft.tistory.com', 'TEST_PASSED', '2025-01-02 07:00:00', '2025-01-02 13:55:58'),
(1, 4, 2, 31, 'ACTIVE', 'https://user1-tech.tistory.com', 'TEST_PASSED', '2025-01-02 07:30:00', '2025-01-02 13:55:58'),
(2, 9, 167, 32, 'ACTIVE', 'https://blog.naver.com/user2_travel', 'TEST_PASSED', '2025-01-02 08:00:00', '2025-01-02 15:30:30'),
(3, 14, 168, 33, 'ACTIVE', 'https://user3-review.tistory.com', 'TEST_PASSED', '2025-01-03 07:30:00', '2025-01-03 14:25:55'),
(4, 19, 169, 34, 'ACTIVE', 'https://blog.naver.com/user4_pet', 'TEST_PASSED', '2025-01-03 08:00:00', '2025-01-03 14:25:55'),
(5, 24, 170, 35, 'ACTIVE', 'https://user5-movie.tistory.com', 'TEST_PASSED', '2025-01-03 08:30:00', '2025-01-03 14:25:55'),
(6, 29, 1, 36, 'ACTIVE', 'https://blog.naver.com/user6_cook', 'TEST_PASSED', '2025-01-04 07:30:00', '2025-01-04 13:35:35'),
(7, 34, 166, 37, 'ACTIVE', 'https://user7-garden.tistory.com', 'TEST_PASSED', '2025-01-04 08:00:00', '2025-01-04 13:35:35'),
(8, 39, 171, 38, 'ACTIVE', 'https://blog.naver.com/user8_diy', 'TEST_PASSED', '2025-01-04 08:30:00', '2025-01-04 13:35:35'),
(9, 44, 172, 39, 'ACTIVE', 'https://user9-design.tistory.com', 'TEST_PASSED', '2025-01-05 08:00:00', '2025-01-05 09:30:58'),
(10, 49, 173, 40, 'ACTIVE', 'https://blog.naver.com/user10_vlog', 'TEST_PASSED', '2025-01-05 08:30:00', '2025-01-05 09:30:58');

-- 2025년 1월 이후 ~ 12월 (ai_content 41번 이후에 대응, 대량 데이터)
-- user_blog_id는 1~50 범위에서 랜덤 선택
-- recurrence_rule_id는 1~100 범위에서 랜덤 선택
INSERT INTO workflow (user_id, user_blog_id, trend_category_id, recurrence_rule_id, status, site_url, test_status, created_at, updated_at)
SELECT
    FLOOR(1 + (RAND() * 10)) as user_id,
    FLOOR(1 + (RAND() * 50)) as user_blog_id,  -- user_blog 1~50
    CASE (FLOOR(RAND() * 10))
        WHEN 0 THEN 1
        WHEN 1 THEN 167
        WHEN 2 THEN 168
        WHEN 3 THEN 169
        WHEN 4 THEN 170
        WHEN 5 THEN 166
        WHEN 6 THEN 171
        WHEN 7 THEN 172
        WHEN 8 THEN 173
        ELSE 2
    END as trend_category_id,
    FLOOR(1 + (RAND() * 100)) as recurrence_rule_id,  -- recurrence_rule 1~100
    CASE (FLOOR(RAND() * 10))
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'INACTIVE'
        WHEN 8 THEN 'COMPLETED'
        ELSE 'ACTIVE'
    END as status,
    CASE (FLOOR(RAND() * 2))
        WHEN 0 THEN CONCAT('https://blog.naver.com/auto_blog', FLOOR(1 + RAND() * 100))
        ELSE CONCAT('https://auto-blog', FLOOR(1 + RAND() * 100), '.tistory.com')
    END as site_url,
    CASE (FLOOR(RAND() * 20))
        WHEN 0 THEN 'TEST_FAILED'
        WHEN 1 THEN 'TESTING'
        WHEN 2 THEN 'NOT_TESTED'
        ELSE 'TEST_PASSED'
    END as test_status,
    DATE_ADD('2025-01-06', INTERVAL seq DAY) as created_at,
    DATE_ADD('2025-01-06', INTERVAL seq DAY) as updated_at
FROM (
    SELECT @row := @row + 1 as seq
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) t3,
         (SELECT @row := 0) r
    WHERE @row < 500
) nums
WHERE DATE_ADD('2025-01-06', INTERVAL seq DAY) <= '2025-12-23';
