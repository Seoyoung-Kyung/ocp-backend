-- ============================================
-- 사용자 블로그 (user_blog)
-- blog_type_id를 참조
-- blog_type: 1(네이버 블로그), 2(티스토리)
-- 주의: UserBlog 엔티티에는 user_id 컬럼이 없음 (user와의 관계 매핑 없음)
-- ============================================

-- 기본 사용자 블로그 (1~50번)
INSERT INTO `user_blog` (`user_blog_id`, `blog_type_id`, `account_id`, `account_pw`, `blog_url`, `created_at`, `updated_at`)
VALUES
-- 블로그 1~5
(1, 1, 'user1_naver', 'password123', 'https://blog.naver.com/user1_naver', '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(2, 2, 'user1_tistory', 'password123', 'https://user1.tistory.com', '2024-01-05 00:00:00', '2024-01-05 00:00:00'),
(3, 1, 'user1_fashion', 'password123', 'https://blog.naver.com/user1_fashion', '2024-02-01 00:00:00', '2024-02-01 00:00:00'),
(4, 2, 'user1_tech', 'password123', 'https://user1-tech.tistory.com', '2024-03-01 00:00:00', '2024-03-01 00:00:00'),
(5, 1, 'user1_food', 'password123', 'https://blog.naver.com/user1_food', '2024-04-01 00:00:00', '2024-04-01 00:00:00'),

-- 블로그 6~10
(6, 2, 'user2_tistory', 'password456', 'https://user2.tistory.com', '2024-01-10 00:00:00', '2024-01-10 00:00:00'),
(7, 1, 'user2_naver', 'password456', 'https://blog.naver.com/user2_naver', '2024-02-10 00:00:00', '2024-02-10 00:00:00'),
(8, 2, 'user2_beauty', 'password456', 'https://user2-beauty.tistory.com', '2024-03-10 00:00:00', '2024-03-10 00:00:00'),
(9, 1, 'user2_travel', 'password456', 'https://blog.naver.com/user2_travel', '2024-04-10 00:00:00', '2024-04-10 00:00:00'),
(10, 2, 'user2_lifestyle', 'password456', 'https://user2-life.tistory.com', '2024-05-10 00:00:00', '2024-05-10 00:00:00'),

-- 블로그 11~15
(11, 1, 'user3_naver', 'password789', 'https://blog.naver.com/user3_naver', '2024-01-15 00:00:00', '2024-01-15 00:00:00'),
(12, 2, 'user3_tistory', 'password789', 'https://user3.tistory.com', '2024-02-15 00:00:00', '2024-02-15 00:00:00'),
(13, 1, 'user3_game', 'password789', 'https://blog.naver.com/user3_game', '2024-03-15 00:00:00', '2024-03-15 00:00:00'),
(14, 2, 'user3_review', 'password789', 'https://user3-review.tistory.com', '2024-04-15 00:00:00', '2024-04-15 00:00:00'),
(15, 1, 'user3_music', 'password789', 'https://blog.naver.com/user3_music', '2024-05-15 00:00:00', '2024-05-15 00:00:00'),

-- 블로그 16~20
(16, 2, 'user4_tistory', 'password101', 'https://user4.tistory.com', '2024-01-20 00:00:00', '2024-01-20 00:00:00'),
(17, 1, 'user4_naver', 'password101', 'https://blog.naver.com/user4_naver', '2024-02-20 00:00:00', '2024-02-20 00:00:00'),
(18, 2, 'user4_sports', 'password101', 'https://user4-sports.tistory.com', '2024-03-20 00:00:00', '2024-03-20 00:00:00'),
(19, 1, 'user4_pet', 'password101', 'https://blog.naver.com/user4_pet', '2024-04-20 00:00:00', '2024-04-20 00:00:00'),
(20, 2, 'user4_finance', 'password101', 'https://user4-finance.tistory.com', '2024-05-20 00:00:00', '2024-05-20 00:00:00'),

-- 블로그 21~25
(21, 1, 'user5_naver', 'password202', 'https://blog.naver.com/user5_naver', '2024-01-25 00:00:00', '2024-01-25 00:00:00'),
(22, 2, 'user5_tistory', 'password202', 'https://user5.tistory.com', '2024-02-25 00:00:00', '2024-02-25 00:00:00'),
(23, 1, 'user5_book', 'password202', 'https://blog.naver.com/user5_book', '2024-03-25 00:00:00', '2024-03-25 00:00:00'),
(24, 2, 'user5_movie', 'password202', 'https://user5-movie.tistory.com', '2024-04-25 00:00:00', '2024-04-25 00:00:00'),
(25, 1, 'user5_health', 'password202', 'https://blog.naver.com/user5_health', '2024-05-25 00:00:00', '2024-05-25 00:00:00'),

-- 블로그 26~30
(26, 2, 'user6_tistory', 'password303', 'https://user6.tistory.com', '2024-02-01 00:00:00', '2024-02-01 00:00:00'),
(27, 1, 'user6_naver', 'password303', 'https://blog.naver.com/user6_naver', '2024-02-10 00:00:00', '2024-02-10 00:00:00'),
(28, 2, 'user6_interior', 'password303', 'https://user6-interior.tistory.com', '2024-03-01 00:00:00', '2024-03-01 00:00:00'),
(29, 1, 'user6_cook', 'password303', 'https://blog.naver.com/user6_cook', '2024-04-01 00:00:00', '2024-04-01 00:00:00'),
(30, 2, 'user6_photo', 'password303', 'https://user6-photo.tistory.com', '2024-05-01 00:00:00', '2024-05-01 00:00:00'),

-- 블로그 31~35
(31, 1, 'user7_naver', 'password404', 'https://blog.naver.com/user7_naver', '2024-02-05 00:00:00', '2024-02-05 00:00:00'),
(32, 2, 'user7_tistory', 'password404', 'https://user7.tistory.com', '2024-02-15 00:00:00', '2024-02-15 00:00:00'),
(33, 1, 'user7_auto', 'password404', 'https://blog.naver.com/user7_auto', '2024-03-05 00:00:00', '2024-03-05 00:00:00'),
(34, 2, 'user7_garden', 'password404', 'https://user7-garden.tistory.com', '2024-04-05 00:00:00', '2024-04-05 00:00:00'),
(35, 1, 'user7_edu', 'password404', 'https://blog.naver.com/user7_edu', '2024-05-05 00:00:00', '2024-05-05 00:00:00'),

-- 블로그 36~40
(36, 2, 'user8_tistory', 'password505', 'https://user8.tistory.com', '2024-02-12 00:00:00', '2024-02-12 00:00:00'),
(37, 1, 'user8_naver', 'password505', 'https://blog.naver.com/user8_naver', '2024-02-20 00:00:00', '2024-02-20 00:00:00'),
(38, 2, 'user8_parenting', 'password505', 'https://user8-parent.tistory.com', '2024-03-12 00:00:00', '2024-03-12 00:00:00'),
(39, 1, 'user8_diy', 'password505', 'https://blog.naver.com/user8_diy', '2024-04-12 00:00:00', '2024-04-12 00:00:00'),
(40, 2, 'user8_cafe', 'password505', 'https://user8-cafe.tistory.com', '2024-05-12 00:00:00', '2024-05-12 00:00:00'),

-- 블로그 41~45
(41, 1, 'user9_naver', 'password606', 'https://blog.naver.com/user9_naver', '2024-02-18 00:00:00', '2024-02-18 00:00:00'),
(42, 2, 'user9_tistory', 'password606', 'https://user9.tistory.com', '2024-02-25 00:00:00', '2024-02-25 00:00:00'),
(43, 1, 'user9_vintage', 'password606', 'https://blog.naver.com/user9_vintage', '2024-03-18 00:00:00', '2024-03-18 00:00:00'),
(44, 2, 'user9_design', 'password606', 'https://user9-design.tistory.com', '2024-04-18 00:00:00', '2024-04-18 00:00:00'),
(45, 1, 'user9_art', 'password606', 'https://blog.naver.com/user9_art', '2024-05-18 00:00:00', '2024-05-18 00:00:00'),

-- 블로그 46~50
(46, 2, 'user10_tistory', 'password707', 'https://user10.tistory.com', '2024-02-22 00:00:00', '2024-02-22 00:00:00'),
(47, 1, 'user10_naver', 'password707', 'https://blog.naver.com/user10_naver', '2024-03-01 00:00:00', '2024-03-01 00:00:00'),
(48, 2, 'user10_craft', 'password707', 'https://user10-craft.tistory.com', '2024-03-22 00:00:00', '2024-03-22 00:00:00'),
(49, 1, 'user10_vlog', 'password707', 'https://blog.naver.com/user10_vlog', '2024-04-22 00:00:00', '2024-04-22 00:00:00'),
(50, 2, 'user10_daily', 'password707', 'https://user10-daily.tistory.com', '2024-05-22 00:00:00', '2024-05-22 00:00:00');

-- 추가 사용자 블로그 (51~200번): 대량 데이터 생성
INSERT INTO `user_blog` (`blog_type_id`, `account_id`, `account_pw`, `blog_url`, `created_at`, `updated_at`)
SELECT
    CASE (seq % 2)
        WHEN 0 THEN 1  -- 네이버 블로그
        ELSE 2         -- 티스토리
    END as blog_type_id,
    CONCAT('auto_blog', seq) as account_id,
    CONCAT('password', FLOOR(1000 + (RAND() * 9000))) as account_pw,
    CASE (seq % 2)
        WHEN 0 THEN CONCAT('https://blog.naver.com/auto_blog', seq)
        ELSE CONCAT('https://auto-blog', seq, '.tistory.com')
    END as blog_url,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 180) DAY) as created_at,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 180) DAY) as updated_at
FROM (
    SELECT @row := @row + 1 as seq
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT 0 UNION ALL SELECT 1) t3,
         (SELECT @row := 50) r
    WHERE @row < 200
) nums;
