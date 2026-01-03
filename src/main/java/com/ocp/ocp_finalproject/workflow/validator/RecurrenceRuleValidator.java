package com.ocp.ocp_finalproject.workflow.validator;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ocp.ocp_finalproject.common.exception.ErrorCode.*;

@Component
public class RecurrenceRuleValidator {

    public void validate(RecurrenceRuleDto dto) {
        if (dto == null) {
            throw new CustomException(RECURRENCE_RULE_INVALID);
        }

        validateRepeatType(dto);
        validateStartAndEndDate(dto);

        switch (dto.getRepeatType()) {
            case ONCE -> validateOnce(dto);
            case DAILY -> validateDaily(dto);
            case WEEKLY -> validateWeekly(dto);
            case MONTHLY -> validateMonthly(dto);
            case CUSTOM -> validateCustom(dto);
        }
    }

    private void validateRepeatType(RecurrenceRuleDto dto) {
        if (dto.getRepeatType() == null) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "반복 유형은 필수입니다");
        }
    }

    private void validateStartAndEndDate(RecurrenceRuleDto dto) {
        if (dto.getStartAt() == null) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "시작 일시는 필수입니다");
        }

        if (dto.getEndAt() != null && dto.getEndAt().isBefore(dto.getStartAt())) {
            throw new CustomException(RECURRENCE_RULE_DATE_RANGE_INVALID);
        }
    }

    private void validateOnce(RecurrenceRuleDto dto) {
        if (dto.getRepeatInterval() != null) {
            throw new CustomException(RECURRENCE_RULE_FIELD_NOT_ALLOWED, "1회 실행은 반복 간격을 지정할 수 없습니다");
        }
    }

    private void validateDaily(RecurrenceRuleDto dto) {
        validateRepeatInterval(dto.getRepeatInterval());
        validateTimesOfDay(dto.getTimesOfDay());

        if (isNotEmpty(dto.getDaysOfWeek())) {
            throw new CustomException(RECURRENCE_RULE_FIELD_NOT_ALLOWED, "매일 반복은 요일을 지정할 수 없습니다");
        }
        if (isNotEmpty(dto.getDaysOfMonth())) {
            throw new CustomException(RECURRENCE_RULE_FIELD_NOT_ALLOWED, "매일 반복은 날짜를 지정할 수 없습니다");
        }
    }

    private void validateWeekly(RecurrenceRuleDto dto) {
        validateRepeatInterval(dto.getRepeatInterval());
        validateDaysOfWeek(dto.getDaysOfWeek());
        validateTimesOfDay(dto.getTimesOfDay());

        if (isNotEmpty(dto.getDaysOfMonth())) {
            throw new CustomException(RECURRENCE_RULE_FIELD_NOT_ALLOWED, "매주 반복은 날짜를 지정할 수 없습니다");
        }
    }

    private void validateMonthly(RecurrenceRuleDto dto) {
        validateRepeatInterval(dto.getRepeatInterval());
        validateDaysOfMonth(dto.getDaysOfMonth());
        validateTimesOfDay(dto.getTimesOfDay());

        if (isNotEmpty(dto.getDaysOfWeek())) {
            throw new CustomException(RECURRENCE_RULE_FIELD_NOT_ALLOWED, "매달 반복은 요일을 지정할 수 없습니다");
        }
    }

    private void validateCustom(RecurrenceRuleDto dto) {
        // Custom은 유연하게 허용
        if (dto.getRepeatInterval() != null && dto.getRepeatInterval() < 1) {
            throw new CustomException(RECURRENCE_RULE_INVALID_FIELD_VALUE, "반복 간격은 1 이상이어야 합니다");
        }

        if (isNotEmpty(dto.getDaysOfWeek())) {
            validateDaysOfWeekValues(dto.getDaysOfWeek());
        }

        if (isNotEmpty(dto.getDaysOfMonth())) {
            validateDaysOfMonthValues(dto.getDaysOfMonth());
        }

        if (isNotEmpty(dto.getTimesOfDay())) {
            validateTimesOfDayFormat(dto.getTimesOfDay());
        }
    }

    private void validateRepeatInterval(Integer repeatInterval) {
        if (repeatInterval == null) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "반복 간격은 필수입니다");
        }
        if (repeatInterval < 1) {
            throw new CustomException(RECURRENCE_RULE_INVALID_FIELD_VALUE, "반복 간격은 1 이상이어야 합니다");
        }
    }

    private void validateDaysOfWeek(List<Integer> daysOfWeek) {
        if (isEmpty(daysOfWeek)) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "요일 선택은 필수입니다");
        }
        validateDaysOfWeekValues(daysOfWeek);
    }

    private void validateDaysOfWeekValues(List<Integer> daysOfWeek) {
        for (Integer day : daysOfWeek) {
            if (day == null || day < 1 || day > 7) {
                throw new CustomException(RECURRENCE_RULE_INVALID_FIELD_VALUE, "요일은 1-7 사이 값이어야 합니다");
            }
        }
    }

    private void validateDaysOfMonth(List<Integer> daysOfMonth) {
        if (isEmpty(daysOfMonth)) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "날짜 선택은 필수입니다");
        }
        validateDaysOfMonthValues(daysOfMonth);
    }

    private void validateDaysOfMonthValues(List<Integer> daysOfMonth) {
        for (Integer day : daysOfMonth) {
            if (day == null || day < 1 || day > 31) {
                throw new CustomException(RECURRENCE_RULE_INVALID_FIELD_VALUE, "날짜는 1-31 사이 값이어야 합니다");
            }
        }
    }

    private void validateTimesOfDay(List<String> timesOfDay) {
        if (isEmpty(timesOfDay)) {
            throw new CustomException(RECURRENCE_RULE_REQUIRED_FIELD_MISSING, "실행 시간은 필수입니다");
        }
        validateTimesOfDayFormat(timesOfDay);
    }

    private void validateTimesOfDayFormat(List<String> timesOfDay) {
        for (String time : timesOfDay) {
            if (!isValidTimeFormat(time)) {
                throw new CustomException(RECURRENCE_RULE_INVALID_FIELD_VALUE,
                        "시간 형식이 올바르지 않습니다 (HH:mm)");
            }
        }
    }

    private boolean isValidTimeFormat(String time) {
        if (time == null || !time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }
}