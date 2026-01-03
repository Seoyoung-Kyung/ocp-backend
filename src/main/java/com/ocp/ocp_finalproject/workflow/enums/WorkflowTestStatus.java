package com.ocp.ocp_finalproject.workflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkflowTestStatus {

    NOT_TESTED("테스트 전", "아직 테스트가 시작되지 않은 상태"),
    TESTING("테스트 중", "콘텐츠 생성 또는 블로그 업로드 테스트를 진행 중인 상태"),
    TEST_PASSED("테스트 성공", "모든 단계(콘텐츠 생성 + 블로그 업로드) 성공"),
    TEST_FAILED("테스트 실패", "테스트 중 하나 이상의 단계 실패");

    private final String displayName;
    private final String description;

    /**
     * 테스트 미실행 여부
     */
    public boolean isNotTested() {
        return this == NOT_TESTED;
    }

    /**
     * 테스트 완료 여부
     */
    public boolean isCompleted() {
        return this == TEST_PASSED || this == TEST_FAILED;
    }

    /**
     * 테스트 성공 여부
     */
    public boolean isPassed() {
        return this == TEST_PASSED;
    }
}
