package com.ocp.ocp_finalproject.workflow.util;

import com.ocp.ocp_finalproject.workflow.domain.RecurrenceRule;
import com.ocp.ocp_finalproject.workflow.enums.RepeatType;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RecurrenceRule 엔티티에 저장된 정보를 Quartz Cron 표현식으로 변환한다.
 */
public class RecurrenceRuleCronConverter {

    public static String toCron(RecurrenceRule rule) {
        List<String> expressions = toCronExpressions(rule);
        if (expressions.isEmpty()) {
            throw new IllegalStateException("하나 이상의 Cron 표현식을 생성해야 합니다.");
        }
        return expressions.get(0);
    }

    public static List<String> toCronExpressions(RecurrenceRule rule) {
        return toCronExpressionsInternal(rule, null);
    }

    public static List<String> toCronExpressionsWithOffset(RecurrenceRule rule, Duration offset) {
        return toCronExpressionsInternal(rule, offset);
    }

    private static List<String> toCronExpressionsInternal(RecurrenceRule rule, Duration offset) {
        if (rule == null) {
            throw new IllegalArgumentException("RecurrenceRule이 null입니다.");
        }

        RepeatType repeatType = rule.getRepeatType() != null ? rule.getRepeatType() : RepeatType.DAILY;

        LocalDateTime startAt = rule.getStartAt();
        CronComponents cronComponents = new CronComponents();

        switch (repeatType) {
            case ONCE -> {
                if (startAt == null) {
                    throw new IllegalArgumentException("ONCE 반복 유형은 startAt 값이 필요합니다.");
                }
                LocalDateTime effectiveStart = offset != null && !offset.isZero() ? startAt.plus(offset) : startAt;
                cronComponents.dayOfMonth = String.valueOf(effectiveStart.getDayOfMonth());
                cronComponents.month = String.valueOf(effectiveStart.getMonthValue());
                cronComponents.dayOfWeek = "?";
                cronComponents.year = String.valueOf(effectiveStart.getYear());
                String minute = formatNumber(effectiveStart.getMinute());
                String hour = formatNumber(effectiveStart.getHour());
                return List.of(cronComponents.build(minute, hour));
            }
            case DAILY -> {
                cronComponents.dayOfMonth = everyNDays(rule.getRepeatInterval(), startAt);
                cronComponents.dayOfWeek = "?";
            }
            case WEEKLY -> {
                cronComponents.dayOfMonth = "?";
                cronComponents.dayOfWeek = formatDayOfWeek(rule.getDaysOfWeek(), startAt);
            }
            case MONTHLY -> {
                cronComponents.dayOfMonth = formatDayOfMonth(rule.getDaysOfMonth(), startAt);
                cronComponents.month = everyNMonths(rule.getRepeatInterval(), startAt);
                cronComponents.dayOfWeek = "?";
            }
            case CUSTOM -> {
                if (hasValues(rule.getDaysOfMonth())) {
                    cronComponents.dayOfMonth = formatDayOfMonth(rule.getDaysOfMonth(), startAt);
                    cronComponents.dayOfWeek = "?";
                } else if (hasValues(rule.getDaysOfWeek())) {
                    cronComponents.dayOfMonth = "?";
                    cronComponents.dayOfWeek = formatDayOfWeek(rule.getDaysOfWeek(), startAt);
                } else {
                    cronComponents.dayOfMonth = everyNDays(rule.getRepeatInterval(), startAt);
                    cronComponents.dayOfWeek = "?";
                }
            }
            default -> {
                cronComponents.dayOfMonth = everyNDays(rule.getRepeatInterval(), startAt);
                cronComponents.dayOfWeek = "?";
            }
        }

        List<LocalTime> executionTimes = resolveTimes(rule.getTimesOfDay(), startAt);
        if (offset != null && !offset.isZero()) {
            executionTimes = executionTimes.stream()
                    .map(time -> time.plus(offset))
                    .collect(Collectors.toList());
        }
        return buildCronExpressions(cronComponents, executionTimes);
    }

    private static List<String> buildCronExpressions(CronComponents cronComponents, List<LocalTime> executionTimes) {
        Optional<CronTimeFields> combinedFields = tryCombineTimes(executionTimes);
        if (combinedFields.isPresent()) {
            CronTimeFields fields = combinedFields.get();
            return List.of(cronComponents.build(fields.minute(), fields.hour()));
        }

        return executionTimes.stream()
                .map(time -> cronComponents.build(formatNumber(time.getMinute()), formatNumber(time.getHour())))
                .collect(Collectors.toList());
    }

    private static List<LocalTime> resolveTimes(List<String> timesOfDay, LocalDateTime startAt) {
        List<LocalTime> fromRule = extractTimes(timesOfDay);
        if (!fromRule.isEmpty()) {
            return fromRule;
        }

        if (startAt != null) {
            return List.of(startAt.toLocalTime());
        }

        return List.of(LocalTime.MIDNIGHT);
    }

    private static List<LocalTime> extractTimes(List<String> timesOfDay) {
        if (timesOfDay == null) {
            return Collections.emptyList();
        }

        LinkedHashSet<LocalTime> orderedTimes = new LinkedHashSet<>();
        for (String timeValue : timesOfDay) {
            if (timeValue == null || timeValue.isBlank()) {
                continue;
            }
            try {
                orderedTimes.add(LocalTime.parse(timeValue));
            } catch (DateTimeParseException ignored) {
                orderedTimes.add(parseLenient(timeValue));
            }
        }

        return new ArrayList<>(orderedTimes);
    }

    private static LocalTime parseLenient(String value) {
        String[] parts = value.trim().split(":");
        if (parts.length >= 2) {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return LocalTime.of(hour, minute);
        }
        throw new IllegalArgumentException("지원하지 않는 시간 형식입니다. value=" + value);
    }

    private static String everyNDays(Integer interval, LocalDateTime startAt) {
        if (interval == null || interval <= 1) {
            return "*";
        }
        int startDay = startAt != null ? startAt.getDayOfMonth() : 1;
        return startDay + "/" + interval;
    }

    private static String everyNMonths(Integer interval, LocalDateTime startAt) {
        if (interval == null || interval <= 1) {
            return "*";
        }
        int startMonth = startAt != null ? startAt.getMonthValue() : 1;
        return startMonth + "/" + interval;
    }

    private static boolean hasValues(List<?> values) {
        return values != null && !values.isEmpty();
    }

    private static String formatDayOfMonth(List<Integer> daysOfMonth, LocalDateTime startAt) {
        List<Integer> days = sanitizeIntegerList(daysOfMonth);
        if (days.isEmpty() && startAt != null) {
            return String.valueOf(startAt.getDayOfMonth());
        }
        if (days.isEmpty()) {
            return "*";
        }

        return days.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static String formatDayOfWeek(List<Integer> daysOfWeek, LocalDateTime startAt) {
        List<Integer> days = sanitizeIntegerList(daysOfWeek);
        if (days.isEmpty() && startAt != null) {
            days = List.of(startAt.getDayOfWeek().getValue());
        }

        if (days.isEmpty()) {
            return "*";
        }

        return days.stream()
                .map(RecurrenceRuleCronConverter::toCronDayOfWeek)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> sanitizeIntegerList(List<Integer> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static String toCronDayOfWeek(Integer value) {
        if (value < 1 || value > 7) {
            throw new IllegalArgumentException("요일 값은 1~7 사이여야 합니다. value=" + value);
        }
        DayOfWeek dayOfWeek = DayOfWeek.of(value);
        return dayOfWeek.name().substring(0, 3);
    }

    private static String formatNumber(int value) {
        return String.format("%02d", value);
    }

    private static Optional<CronTimeFields> tryCombineTimes(List<LocalTime> executionTimes) {
        if (executionTimes.isEmpty()) {
            return Optional.empty();
        }

        if (executionTimes.size() == 1) {
            LocalTime time = executionTimes.get(0);
            return Optional.of(new CronTimeFields(formatNumber(time.getMinute()), formatNumber(time.getHour())));
        }

        Set<LocalTime> uniqueTimes = Set.copyOf(executionTimes);
        Set<Integer> minutes = executionTimes.stream()
                .map(LocalTime::getMinute)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Integer> hours = executionTimes.stream()
                .map(LocalTime::getHour)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        long expectedSize = (long) minutes.size() * hours.size();
        if (expectedSize != uniqueTimes.size()) {
            return Optional.empty();
        }

        for (Integer hour : hours) {
            for (Integer minute : minutes) {
                if (!uniqueTimes.contains(LocalTime.of(hour, minute))) {
                    return Optional.empty();
                }
            }
        }

        String minuteField = minutes.stream()
                .sorted()
                .map(RecurrenceRuleCronConverter::formatNumber)
                .collect(Collectors.joining(","));
        String hourField = hours.stream()
                .sorted()
                .map(RecurrenceRuleCronConverter::formatNumber)
                .collect(Collectors.joining(","));

        return Optional.of(new CronTimeFields(minuteField, hourField));
    }

    private static class CronComponents {
        private String second = "0";
        private String dayOfMonth = "*";
        private String month = "*";
        private String dayOfWeek = "?";
        private String year = "";

        private String build(String minute, String hour) {
            String cron = String.format("%s %s %s %s %s %s", second, minute, hour, dayOfMonth, month, dayOfWeek);
            if (!year.isBlank()) {
                cron = cron + " " + year;
            }
            return cron;
        }
    }

    private record CronTimeFields(String minute, String hour) {
    }
}
