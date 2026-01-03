package com.ocp.ocp_finalproject.common.config.init;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class DummyDataInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Value("classpath:db/seed/dummy_data.sql")
    private Resource dummyDataSql;

    @Value("classpath:db/seed/system_daily_statistics.sql")
    private Resource systemDailyStatisticsSql;

    @Value("classpath:db/seed/ai_usage_log.sql")
    private Resource aiUsageLogSql;

    @Value("classpath:db/seed/system_logs.sql")
    private Resource systemLogsSql;

    @Value("classpath:db/seed/ai_content.sql")
    private Resource aiContentSql;

    @Value("classpath:db/seed/workflow.sql")
    private Resource workflowSql;

    @Value("classpath:db/seed/work.sql")
    private Resource workSql;

    @Value("classpath:db/seed/trend_category.sql")
    private Resource trendCategorySql;

    @Value("classpath:db/seed/common_code.sql")
    private Resource commonCodeSql;

    @Value("classpath:db/seed/recurrence_rule.sql")
    private Resource recurrenceRuleSql;

    @Value("classpath:db/seed/user_blog.sql")
    private Resource userBlogSql;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========================================");
        log.info("[Init] DummyDataInitializer start");
        log.info("========================================");

        try {
            // ========== 1단계: 독립 테이블 (외래키 참조 없음) ==========

            // 1. trend_category 로드
            log.info("[Init] 1. trend_category 데이터 로드 시작");
            ResourceDatabasePopulator categoryPopulator = new ResourceDatabasePopulator(trendCategorySql);
            categoryPopulator.setSeparator(";");
            categoryPopulator.setContinueOnError(true);
            categoryPopulator.execute(dataSource);
            log.info("[Init] 1. trend_category 데이터 로드 완료");

            // 2. common_code 로드
            log.info("[Init] 2. common_code 데이터 로드 시작");
            ResourceDatabasePopulator codePopulator = new ResourceDatabasePopulator(commonCodeSql);
            codePopulator.setSeparator(";");
            codePopulator.setContinueOnError(true);
            codePopulator.execute(dataSource);
            log.info("[Init] 2. common_code 데이터 로드 완료");

            // ========== 2단계: 기본 데이터 (user, auth, blog_type 등) ==========

            // 3. 기본 더미 데이터 로드 (user, auth, blog_type 포함)
            log.info("[Init] 3. 기본 더미 데이터 로드 시작 (user, auth, blog_type)");
            ResourceDatabasePopulator dummyPopulator = new ResourceDatabasePopulator(dummyDataSql);
            dummyPopulator.setSeparator(";");
            dummyPopulator.setContinueOnError(true);
            dummyPopulator.execute(dataSource);
            log.info("[Init] 3. 기본 더미 데이터 로드 완료");

            // ========== 3단계: user, blog_type 참조 테이블 ==========

            // 4. user_blog 로드 (user, blog_type 참조)
            log.info("[Init] 4. user_blog 데이터 로드 시작");
            ResourceDatabasePopulator userBlogPopulator = new ResourceDatabasePopulator(userBlogSql);
            userBlogPopulator.setSeparator(";");
            userBlogPopulator.setContinueOnError(true);
            userBlogPopulator.execute(dataSource);
            log.info("[Init] 4. user_blog 데이터 로드 완료");

            // 5. recurrence_rule 로드 (독립 테이블)
            log.info("[Init] 5. recurrence_rule 데이터 로드 시작");
            ResourceDatabasePopulator recurrencePopulator = new ResourceDatabasePopulator(recurrenceRuleSql);
            recurrencePopulator.setSeparator(";");
            recurrencePopulator.setContinueOnError(true);
            recurrencePopulator.execute(dataSource);
            log.info("[Init] 5. recurrence_rule 데이터 로드 완료");

            // ========== 4단계: workflow 로드 (user, user_blog, trend_category, recurrence_rule 참조) ==========

            // 6. workflow 로드
            log.info("[Init] 6. workflow 데이터 로드 시작");
            ResourceDatabasePopulator workflowPopulator = new ResourceDatabasePopulator(workflowSql);
            workflowPopulator.setSeparator(";");
            workflowPopulator.setContinueOnError(true);
            workflowPopulator.execute(dataSource);
            log.info("[Init] 6. workflow 데이터 로드 완료");

            // ========== 5단계: work 로드 (workflow 참조) ==========

            // 7. work 로드
            log.info("[Init] 7. work 데이터 로드 시작");
            ResourceDatabasePopulator workPopulator = new ResourceDatabasePopulator(workSql);
            workPopulator.setSeparator(";");
            workPopulator.setContinueOnError(true);
            workPopulator.execute(dataSource);
            log.info("[Init] 7. work 데이터 로드 완료");

            // ========== 6단계: ai_content 로드 (work 참조) ==========

            // 8. ai_content 로드
            log.info("[Init] 8. ai_content 데이터 로드 시작");
            ResourceDatabasePopulator aiContentPopulator = new ResourceDatabasePopulator(aiContentSql);
            aiContentPopulator.setSeparator(";");
            aiContentPopulator.setContinueOnError(true);
            aiContentPopulator.execute(dataSource);
            log.info("[Init] 8. ai_content 데이터 로드 완료");

            // ========== 7단계: 통계 및 로그 데이터 (독립) ==========

            // 9. system_daily_statistics 로드
            log.info("[Init] 9. system_daily_statistics 데이터 로드 시작");
            ResourceDatabasePopulator statsPopulator = new ResourceDatabasePopulator(systemDailyStatisticsSql);
            statsPopulator.setSeparator(";");
            statsPopulator.setContinueOnError(true);
            statsPopulator.execute(dataSource);
            log.info("[Init] 9. system_daily_statistics 데이터 로드 완료");

            // 10. ai_usage_log 로드
            log.info("[Init] 10. ai_usage_log 데이터 로드 시작");
            ResourceDatabasePopulator aiLogPopulator = new ResourceDatabasePopulator(aiUsageLogSql);
            aiLogPopulator.setSeparator(";");
            aiLogPopulator.setContinueOnError(true);
            aiLogPopulator.execute(dataSource);
            log.info("[Init] 10. ai_usage_log 데이터 로드 완료");

            // 11. system_logs 로드
            log.info("[Init] 11. system_logs 데이터 로드 시작");
            ResourceDatabasePopulator sysLogPopulator = new ResourceDatabasePopulator(systemLogsSql);
            sysLogPopulator.setSeparator(";");
            sysLogPopulator.setContinueOnError(true);
            sysLogPopulator.execute(dataSource);
            log.info("[Init] 11. system_logs 데이터 로드 완료");

            log.info("[Init] 모든 SQL 실행 완료");

            // 데이터 확인
            try (Connection c = dataSource.getConnection();
                 Statement st = c.createStatement()) {

                ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM user");
                if (rs1.next()) {
                    log.info("[Init] user 개수: {}", rs1.getInt(1));
                }

                ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM workflow");
                if (rs2.next()) {
                    log.info("[Init] workflow 개수: {}", rs2.getInt(1));
                }

                ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM recurrence_rule");
                if (rs3.next()) {
                    log.info("[Init] recurrence_rule 개수: {}", rs3.getInt(1));
                }

                ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM work");
                if (rs4.next()) {
                    log.info("[Init] work 개수: {}", rs4.getInt(1));
                }


                ResultSet rs6 = st.executeQuery("SELECT COUNT(*) FROM product_crawl");
                if (rs6.next()) {
                    log.info("[Init] product_crawl 개수: {}", rs6.getInt(1));
                }

                ResultSet rs7 = st.executeQuery("SELECT COUNT(*) FROM work_detail_log");
                if (rs7.next()) {
                    log.info("[Init] work_detail_log 개수: {}", rs7.getInt(1));
                }

                ResultSet rs8 = st.executeQuery("SELECT COUNT(*) FROM ai_content");
                if (rs8.next()) {
                    log.info("[Init] ai_content 개수: {}", rs8.getInt(1));
                }

                ResultSet rs10 = st.executeQuery("SELECT COUNT(*) FROM ai_usage_log");
                if (rs10.next()) {
                    log.info("[Init] ai_usage_log 개수: {}", rs10.getInt(1));
                }

                ResultSet rs11 = st.executeQuery("SELECT COUNT(*) FROM notice");
                if (rs11.next()) {
                    log.info("[Init] notice 개수: {}", rs11.getInt(1));
                }

                ResultSet rs12 = st.executeQuery("SELECT COUNT(*) FROM auth");
                if (rs12.next()) {
                    log.info("[Init] auth 개수: {}", rs12.getInt(1));
                }

                ResultSet rs13 = st.executeQuery("SELECT COUNT(*) FROM user_blog");
                if (rs13.next()) {
                    log.info("[Init] user_blog 개수: {}", rs13.getInt(1));
                }

                ResultSet rs14 = st.executeQuery("SELECT COUNT(*) FROM blog_type");
                if (rs14.next()) {
                    log.info("[Init] blog_type 개수: {}", rs14.getInt(1));
                }

                ResultSet rs16 = st.executeQuery("SELECT COUNT(*) FROM trend_category");
                if (rs16.next()) {
                    log.info("[Init] trend_category 개수: {}", rs16.getInt(1));
                }

                ResultSet rs18 = st.executeQuery("SELECT COUNT(*) FROM common_code_group");
                if (rs18.next()) {
                    log.info("[Init] common_code_group 개수: {}", rs18.getInt(1));
                }

                ResultSet rs19 = st.executeQuery("SELECT COUNT(*) FROM common_code");
                if (rs19.next()) {
                    log.info("[Init] common_code 개수: {}", rs19.getInt(1));
                }

                ResultSet rs20 = st.executeQuery("SELECT COUNT(*) FROM system_logs");
                if (rs20.next()) {
                    log.info("[Init] system_logs 개수: {}", rs20.getInt(1));
                }

                ResultSet rs21 = st.executeQuery("SELECT COUNT(*) FROM daily_statistics");
                if (rs21.next()) {
                    log.info("[Init] daily_statistics 개수: {}", rs21.getInt(1));
                }

            }

        } catch (Exception e) {
            log.error("[Init] 에러 발생: ", e);
        }

        log.info("========================================");
        log.info("[Init] DummyDataInitializer done");
        log.info("========================================");
    }
}