package com.ocp.ocp_finalproject.config;

import java.util.Map;

public class TaskStepMapper {

    // Task ID → Step Number
    private static final Map<String, Integer> TASK_STEP_MAP = Map.of(
            "consume_request", 0,
            "select_keyword", 1,
            "send_keyword_webhook", 2,
            "run_crawler", 3,
            "branch_product_path", 4,
            "find_product", 5
    );

    // Task ID → 한글 이름
    private static final Map<String, String> TASK_NAME_MAP = Map.of(
            "consume_request", "요청 수신",
            "select_keyword", "키워드 선택",
            "send_keyword_webhook", "키워드 전송",
            "run_crawler", "상품 크롤링",
            "branch_product_path", "경로 분기",
            "find_product", "상품 선택"
    );

    public static Integer getStepNumber(String taskId) {
        return TASK_STEP_MAP.getOrDefault(taskId, 999);
    }

    public static String getStepName(String taskId) {
        return TASK_NAME_MAP.getOrDefault(taskId, taskId);
    }
}
