package com.ocp.ocp_finalproject.workflow.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import com.ocp.ocp_finalproject.workflow.enums.RepeatType;
import com.ocp.ocp_finalproject.workflow.util.IntegerListConverter;
import com.ocp.ocp_finalproject.workflow.util.RecurrenceRuleFormatter;
import com.ocp.ocp_finalproject.workflow.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recurrence_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecurrenceRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrence_rule_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", length = 50)
    private RepeatType repeatType;

    @Column(name = "repeat_interval")
    private Integer repeatInterval;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "days_of_week", columnDefinition = "JSON")
    private List<Integer> daysOfWeek;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "days_of_month", columnDefinition = "JSON")
    private List<Integer> daysOfMonth;

    @Convert(converter = StringListConverter.class)
    @Column(name = "times_of_day", columnDefinition = "JSON")
    private List<String> timesOfDay;

    @Column(name = "readable_rule", length = 100)
    private String readableRule;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    public static RecurrenceRule create(
            RepeatType repeatType,
            Integer repeatInterval,
            List<Integer> daysOfWeek,
            List<Integer> daysOfMonth,
            List<String> timesOfDay,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        RecurrenceRule recurrenceRule = new RecurrenceRule();
        recurrenceRule.repeatType = repeatType;
        recurrenceRule.repeatInterval = repeatInterval;
        recurrenceRule.daysOfWeek = daysOfWeek;
        recurrenceRule.daysOfMonth = daysOfMonth;
        recurrenceRule.timesOfDay = timesOfDay;
        recurrenceRule.readableRule = RecurrenceRuleFormatter.toReadableString(
                repeatType, repeatInterval, daysOfWeek, daysOfMonth, timesOfDay
        );
        recurrenceRule.startAt = startAt;
        recurrenceRule.endAt = endAt;
        return recurrenceRule;
    }

    public void update(RecurrenceRuleDto rule) {
        this.repeatType = rule.getRepeatType();
        this.repeatInterval = rule.getRepeatInterval();
        this.daysOfWeek = rule.getDaysOfWeek();
        this.daysOfMonth = rule.getDaysOfMonth();
        this.timesOfDay = rule.getTimesOfDay();
        this.readableRule = RecurrenceRuleFormatter.toReadableString(rule);
        this.startAt = rule.getStartAt();
        this.endAt = rule.getEndAt();
    }
}