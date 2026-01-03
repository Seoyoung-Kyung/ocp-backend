package com.ocp.ocp_finalproject.workflow.util;

import com.ocp.ocp_finalproject.workflow.dto.RecurrenceRuleDto;
import com.ocp.ocp_finalproject.workflow.enums.RepeatType;

import java.util.List;
import java.util.stream.Collectors;

public class RecurrenceRuleFormatter {

    public static String toReadableString(RecurrenceRuleDto dto) {
        return toReadableString(
                dto.getRepeatType(),
                dto.getRepeatInterval(),
                dto.getDaysOfWeek(),
                dto.getDaysOfMonth(),
                dto.getTimesOfDay()
        );
    }

    public static String toReadableString(
            RepeatType repeatType,
            Integer repeatInterval,
            List<Integer> daysOfWeek,
            List<Integer> daysOfMonth,
            List<String> timesOfDay
    ) {
        StringBuilder sb = new StringBuilder();

        switch (repeatType) {
            case ONCE:
                return "1회만 실행";
            case DAILY:
                sb.append("매일");
                break;
            case WEEKLY:
                sb.append("매주 ").append(formatDaysOfWeek(daysOfWeek));
                break;
            case MONTHLY:
                sb.append("매달 ").append(formatDaysOfMonth(daysOfMonth));
                break;
            case CUSTOM:
                sb.append("사용자 정의");
                break;
        }

        if (repeatInterval != null && repeatInterval > 1) {
            sb.append(" (").append(repeatInterval).append("번째마다)");
        }

        if (timesOfDay != null && !timesOfDay.isEmpty()) {
            sb.append(" ").append(formatTimesOfDay(timesOfDay)).append("에 실행");
        }

        return sb.toString();

    }

    private static String formatDaysOfWeek(List<Integer> daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return "";
        }

        String days = daysOfWeek.stream()
                .map(RecurrenceRuleFormatter::convertDayToKorean)
                .collect(Collectors.joining(", "));

        return days + "요일";
    }

    private static String formatDaysOfMonth(List<Integer> daysOfMonth) {
        if (daysOfMonth == null || daysOfMonth.isEmpty()) {
            return "";
        }

        return daysOfMonth.stream()
                .map(day -> day + "일")
                .collect(Collectors.joining(", "));
    }

    private static String formatTimesOfDay(List<String> timesOfDay) {
        if (timesOfDay == null || timesOfDay.isEmpty()) {
            return "";
        }

        return String.join(", ", timesOfDay);
    }

    private static String convertDayToKorean(Integer day) {
        return switch (day) {
            case 1 -> "월";
            case 2 -> "화";
            case 3 -> "수";
            case 4 -> "목";
            case 5 -> "금";
            case 6 -> "토";
            case 7 -> "일";
            default -> String.valueOf(day);
        };
    }
}
