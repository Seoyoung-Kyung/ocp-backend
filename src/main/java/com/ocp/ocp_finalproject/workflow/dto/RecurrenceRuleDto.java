package com.ocp.ocp_finalproject.workflow.dto;

import com.ocp.ocp_finalproject.workflow.domain.RecurrenceRule;
import com.ocp.ocp_finalproject.workflow.enums.RepeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecurrenceRuleDto {
    private RepeatType repeatType;

    private Integer repeatInterval;

    private List<Integer> daysOfWeek;

    private List<Integer> daysOfMonth;

    private List<String> timesOfDay;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private String readableRule;

    public static RecurrenceRuleDto from(RecurrenceRule rule) {
        return RecurrenceRuleDto.builder()
                .repeatType(rule.getRepeatType())
                .repeatInterval(rule.getRepeatInterval())
                .daysOfWeek(rule.getDaysOfWeek())
                .daysOfMonth(rule.getDaysOfMonth())
                .timesOfDay(rule.getTimesOfDay())
                .startAt(rule.getStartAt())
                .endAt(rule.getEndAt())
                .readableRule(rule.getReadableRule())
                .build();
    }

}