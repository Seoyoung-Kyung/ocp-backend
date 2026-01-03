package com.ocp.ocp_finalproject.monitoring.util;

import com.ocp.ocp_finalproject.monitoring.dto.ParsedLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class AirflowLogParser {

    // Airflow 시스템 파일 ( 관리자용 )
    private static final Set<String> SYSTEM_FILES = Set.of(
            "taskinstance.py",
            "local_task_job_runner.py",
            "task_command.py",
            "standard_task_runner.py",
            "warnings.py",
            "_client.py",
            "python.py"
    );

    // 로그 라인 정규표현식 : [timestamp] {file.py:line} LEVEL - message
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "\\[(.*?)\\]\\s+\\{(.*?):(\\d+)\\}\\s+(TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL)\\s+-\\s+(.*)"
    );

    /**
     * 사용자용 로그 파싱 (비즈니스 로직만)
     */
    public List<ParsedLog> parseForUser(String logContent) {
        return Arrays.stream(logContent.split("\n"))
                .map(LOG_PATTERN::matcher)
                .filter(Matcher::find)
                .filter(matcher -> isBusinessLogicFile(matcher.group(2)) &&
                                   isUserVisibleLevel(matcher.group(4)))
                .map(matcher -> ParsedLog.builder()
                        .timestamp(matcher.group(1))
                        .fileName(matcher.group(2))
                        .lineNumber(Integer.parseInt(matcher.group(3)))
                        .logLevel(matcher.group(4))
                        .message(matcher.group(5))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 로그 파싱 (모든 로그)
     */
    public List<ParsedLog> parseForAdmin(String logContent) {
        return Arrays.stream(logContent.split("\n"))
                .flatMap(line -> {
                    Matcher matcher = LOG_PATTERN.matcher(line);
                    if (matcher.find()) {
                        return Stream.of(ParsedLog.builder()
                                .timestamp(matcher.group(1))
                                .fileName(matcher.group(2))
                                .lineNumber(Integer.parseInt(matcher.group(3)))
                                .logLevel(matcher.group(4))
                                .message(matcher.group(5))
                                .rawLine(line)
                                .build());
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList());
    }

    private boolean isBusinessLogicFile(String fileName) {
        return !SYSTEM_FILES.contains(fileName);
    }

    private boolean isUserVisibleLevel(String level) {
        return level.equals("INFO") || level.equals("WARN") ||
                level.equals("WARNING") || level.equals("ERROR") || level.equals("FATAL");
    }

};
