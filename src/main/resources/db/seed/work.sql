-- work (작업 실행 - ai_content와 1:1 매칭)
-- status: PENDING, REQUESTED, TREND_KEYWORD_DONE, PRODUCT_SELECTED, CONTENT_GENERATED, BLOG_UPLOAD_PENDING, COMPLETED, FAILED
-- workflow_id는 위에서 생성된 workflow와 매칭

-- 2024년 12월 ~ 2025년 1월 (ai_content 1~40번과 1:1 대응)
INSERT INTO work (workflow_id, status, posting_url, started_at, completed_at, view_count, failure_reason, created_at, updated_at)
VALUES
-- 12월 24일 (ai_content 1~3, workflow 1~3)
(1, 'COMPLETED', 'https://example-blog1.tistory.com/post/1', '2024-12-24 09:15:20', '2024-12-24 10:25:30', 245, NULL, '2024-12-24 09:15:20', '2024-12-24 10:25:30'),
(1, 'COMPLETED', 'https://example-blog1.tistory.com/post/2', '2024-12-24 10:22:40', '2024-12-24 10:30:45', 189, NULL, '2024-12-24 10:22:40', '2024-12-24 10:30:45'),
(2, 'COMPLETED', 'https://blog.naver.com/user1/post1', '2024-12-24 11:30:10', '2024-12-24 11:35:20', 312, NULL, '2024-12-24 11:30:10', '2024-12-24 11:35:20'),

-- 12월 25일 (ai_content 4~5, workflow 4~5)
(4, 'COMPLETED', 'https://blog.naver.com/user2/post1', '2024-12-25 08:45:30', '2024-12-25 14:20:15', 156, NULL, '2024-12-25 08:45:30', '2024-12-25 14:20:15'),
(5, 'COMPLETED', 'https://example-blog3.tistory.com/post/1', '2024-12-25 14:18:25', '2024-12-25 14:25:40', 423, NULL, '2024-12-25 14:18:25', '2024-12-25 14:25:40'),

-- 12월 26일 (ai_content 6~9, workflow 6~9)
(6, 'COMPLETED', 'https://blog.naver.com/user3/post1', '2024-12-26 09:30:12', '2024-12-26 09:35:28', 278, NULL, '2024-12-26 09:30:12', '2024-12-26 09:35:28'),
(7, 'COMPLETED', 'https://example-blog4.tistory.com/post/1', '2024-12-26 10:45:20', '2024-12-26 10:50:35', 567, NULL, '2024-12-26 10:45:20', '2024-12-26 10:50:35'),
(8, 'FAILED', NULL, '2024-12-26 12:20:38', '2024-12-26 12:25:18', 0, 'Network connection timeout during blog upload', '2024-12-26 12:20:38', '2024-12-26 12:25:18'),
(9, 'COMPLETED', 'https://example-blog5.tistory.com/post/1', '2024-12-26 15:35:15', '2024-12-26 15:40:32', 198, NULL, '2024-12-26 15:35:15', '2024-12-26 15:40:32'),

-- 12월 27일 (ai_content 10~13, workflow 10~13)
(10, 'COMPLETED', 'https://blog.naver.com/user5/post1', '2024-12-27 08:20:42', '2024-12-27 08:25:50', 334, NULL, '2024-12-27 08:20:42', '2024-12-27 08:25:50'),
(11, 'COMPLETED', 'https://example-blog6.tistory.com/post/1', '2024-12-27 09:55:30', '2024-12-27 09:58:20', 445, NULL, '2024-12-27 09:55:30', '2024-12-27 09:58:20'),
(12, 'COMPLETED', 'https://blog.naver.com/user6/post1', '2024-12-27 11:40:25', '2024-12-27 11:50:38', 289, NULL, '2024-12-27 11:40:25', '2024-12-27 11:50:38'),
(13, 'COMPLETED', 'https://example-blog7.tistory.com/post/1', '2024-12-27 14:25:10', '2024-12-27 14:30:25', 512, NULL, '2024-12-27 14:25:10', '2024-12-27 14:30:25'),

-- 12월 28일 (ai_content 14~16, workflow 14~16)
(14, 'COMPLETED', 'https://blog.naver.com/user7/post1', '2024-12-28 10:15:38', '2024-12-28 10:20:45', 367, NULL, '2024-12-28 10:15:38', '2024-12-28 10:20:45'),
(15, 'COMPLETED', 'https://example-blog8.tistory.com/post/1', '2024-12-28 13:30:23', '2024-12-28 13:35:30', 421, NULL, '2024-12-28 13:30:23', '2024-12-28 13:35:30'),
(16, 'COMPLETED', 'https://blog.naver.com/user8/post1', '2024-12-28 15:45:16', '2024-12-28 15:50:22', 298, NULL, '2024-12-28 15:45:16', '2024-12-28 15:50:22'),

-- 12월 29일 (ai_content 17~18, workflow 17~18)
(17, 'COMPLETED', 'https://example-blog9.tistory.com/post/1', '2024-12-29 09:20:33', '2024-12-29 09:25:40', 234, NULL, '2024-12-29 09:20:33', '2024-12-29 09:25:40'),
(18, 'COMPLETED', 'https://blog.naver.com/user9/post1', '2024-12-29 11:35:40', '2024-12-29 11:40:55', 456, NULL, '2024-12-29 11:35:40', '2024-12-29 11:40:55'),

-- 12월 30일 (ai_content 19~23, workflow 19~23)
(19, 'COMPLETED', 'https://example-blog10.tistory.com/post/1', '2024-12-30 08:40:20', '2024-12-30 08:45:35', 389, NULL, '2024-12-30 08:40:20', '2024-12-30 08:45:35'),
(20, 'COMPLETED', 'https://blog.naver.com/user10/post1', '2024-12-30 10:25:36', '2024-12-30 10:30:42', 267, NULL, '2024-12-30 10:25:36', '2024-12-30 10:30:42'),
(21, 'COMPLETED', 'https://example-blog1-2.tistory.com/post/1', '2024-12-30 12:50:43', '2024-12-30 12:55:50', 534, NULL, '2024-12-30 12:50:43', '2024-12-30 12:55:50'),
(22, 'COMPLETED', 'https://blog.naver.com/user1-2/post1', '2024-12-30 14:15:26', '2024-12-30 14:20:45', 412, NULL, '2024-12-30 14:15:26', '2024-12-30 14:20:45'),
(23, 'COMPLETED', 'https://example-blog3-2.tistory.com/post/1', '2024-12-30 16:30:13', '2024-12-30 16:35:28', 345, NULL, '2024-12-30 16:30:13', '2024-12-30 16:35:28'),

-- 12월 31일 (ai_content 24~27, workflow 24~27)
(24, 'COMPLETED', 'https://blog.naver.com/user4-2/post1', '2024-12-31 09:10:40', '2024-12-31 09:15:55', 489, NULL, '2024-12-31 09:10:40', '2024-12-31 09:15:55'),
(25, 'COMPLETED', 'https://example-blog5-2.tistory.com/post/1', '2024-12-31 10:45:33', '2024-12-31 10:50:48', 376, NULL, '2024-12-31 10:45:33', '2024-12-31 10:50:48'),
(26, 'COMPLETED', 'https://blog.naver.com/user6-2/post1', '2024-12-31 13:20:46', '2024-12-31 13:25:22', 523, NULL, '2024-12-31 13:20:46', '2024-12-31 13:25:22'),
(27, 'COMPLETED', 'https://example-blog7-2.tistory.com/post/1', '2024-12-31 15:55:20', '2024-12-31 16:00:35', 298, NULL, '2024-12-31 15:55:20', '2024-12-31 16:00:35'),

-- 2025년 1월 (ai_content 28~40, workflow 28~40)
(28, 'COMPLETED', 'https://blog.naver.com/user8-2/post1', '2025-01-01 09:30:13', '2025-01-01 09:35:28', 445, NULL, '2025-01-01 09:30:13', '2025-01-01 09:35:28'),
(29, 'COMPLETED', 'https://example-blog9-2.tistory.com/post/1', '2025-01-01 11:45:26', '2025-01-01 11:50:35', 367, NULL, '2025-01-01 11:45:26', '2025-01-01 11:50:35'),
(30, 'COMPLETED', 'https://blog.naver.com/user10-2/post1', '2025-01-02 08:20:38', '2025-01-02 08:25:52', 512, NULL, '2025-01-02 08:20:38', '2025-01-02 08:25:52'),
(31, 'COMPLETED', 'https://example-blog1-3.tistory.com/post/1', '2025-01-02 10:35:20', '2025-01-02 10:40:35', 289, NULL, '2025-01-02 10:35:20', '2025-01-02 10:40:35'),
(32, 'COMPLETED', 'https://blog.naver.com/user1-3/post1', '2025-01-02 13:50:43', '2025-01-02 13:55:58', 678, NULL, '2025-01-02 13:50:43', '2025-01-02 13:55:58'),
(33, 'COMPLETED', 'https://example-blog3-3.tistory.com/post/1', '2025-01-02 15:25:16', '2025-01-02 15:30:30', 423, NULL, '2025-01-02 15:25:16', '2025-01-02 15:30:30'),
(34, 'COMPLETED', 'https://blog.naver.com/user4-3/post1', '2025-01-03 09:15:31', '2025-01-03 09:20:45', 534, NULL, '2025-01-03 09:15:31', '2025-01-03 09:20:45'),
(35, 'COMPLETED', 'https://example-blog5-3.tistory.com/post/1', '2025-01-03 11:40:23', '2025-01-03 11:45:38', 398, NULL, '2025-01-03 11:40:23', '2025-01-03 11:45:38'),
(36, 'COMPLETED', 'https://blog.naver.com/user6-3/post1', '2025-01-03 14:20:40', '2025-01-03 14:25:55', 456, NULL, '2025-01-03 14:20:40', '2025-01-03 14:25:55'),
(37, 'COMPLETED', 'https://example-blog7-3.tistory.com/post/1', '2025-01-04 08:45:13', '2025-01-04 08:50:28', 312, NULL, '2025-01-04 08:45:13', '2025-01-04 08:50:28'),
(38, 'COMPLETED', 'https://blog.naver.com/user8-3/post1', '2025-01-04 10:55:36', '2025-01-04 11:00:42', 589, NULL, '2025-01-04 10:55:36', '2025-01-04 11:00:42'),
(39, 'COMPLETED', 'https://example-blog9-3.tistory.com/post/1', '2025-01-04 13:30:20', '2025-01-04 13:35:35', 267, NULL, '2025-01-04 13:30:20', '2025-01-04 13:35:35'),
(40, 'COMPLETED', 'https://blog.naver.com/user10-3/post1', '2025-01-05 09:25:46', '2025-01-05 09:30:58', 445, NULL, '2025-01-05 09:25:46', '2025-01-05 09:30:58');

-- 2025년 1월 이후 ~ 12월 (ai_content 41번 이후와 대응, workflow 41번 이후와 대응)
INSERT INTO work (workflow_id, status, posting_url, started_at, completed_at, view_count, failure_reason, created_at, updated_at)
SELECT
    41 + seq as workflow_id,
    CASE (FLOOR(RAND() * 20))
        WHEN 0 THEN 'FAILED'
        WHEN 1 THEN 'PENDING'
        WHEN 2 THEN 'REQUESTED'
        WHEN 3 THEN 'TREND_KEYWORD_DONE'
        WHEN 4 THEN 'PRODUCT_SELECTED'
        WHEN 5 THEN 'CONTENT_GENERATED'
        WHEN 6 THEN 'BLOG_UPLOAD_PENDING'
        ELSE 'COMPLETED'
    END as status,
    CASE
        WHEN FLOOR(RAND() * 20) = 0 THEN NULL
        ELSE CONCAT('https://blog-', FLOOR(1 + RAND() * 100), '.example.com/post/', FLOOR(1 + RAND() * 1000))
    END as posting_url,
    DATE_ADD('2025-01-06', INTERVAL seq DAY) as started_at,
    CASE
        WHEN FLOOR(RAND() * 20) IN (1, 2) THEN NULL
        ELSE DATE_ADD('2025-01-06', INTERVAL seq DAY)
    END as completed_at,
    CASE
        WHEN FLOOR(RAND() * 20) = 0 THEN 0
        ELSE FLOOR(50 + RAND() * 650)
    END as view_count,
    CASE
        WHEN FLOOR(RAND() * 20) = 0 THEN
            CASE (FLOOR(RAND() * 5))
                WHEN 0 THEN 'Network connection timeout'
                WHEN 1 THEN 'Authentication failed'
                WHEN 2 THEN 'Content validation error'
                WHEN 3 THEN 'API rate limit exceeded'
                ELSE 'Unknown error occurred'
            END
        ELSE NULL
    END as failure_reason,
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
