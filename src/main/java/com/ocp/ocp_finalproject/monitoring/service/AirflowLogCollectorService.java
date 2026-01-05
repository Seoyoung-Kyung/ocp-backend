package com.ocp.ocp_finalproject.monitoring.service;

import com.ocp.ocp_finalproject.monitoring.domain.WorkDetailLog;
import com.ocp.ocp_finalproject.monitoring.enums.LogLevel;
import com.ocp.ocp_finalproject.monitoring.enums.StepStatus;
import com.ocp.ocp_finalproject.monitoring.repository.WorkDetailLogRepository;
import com.ocp.ocp_finalproject.monitoring.util.AirflowLogParser;
import com.ocp.ocp_finalproject.monitoring.dto.ParsedLog;
import com.ocp.ocp_finalproject.config.TaskStepMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirflowLogCollectorService {

    private final AirflowLogParser logParser;
    private final WorkDetailLogRepository logRepository;

    @Value("${airflow.log.base-path:airflow/logs}")
    private String airflowLogBasePath;

    /**
     * Airflow 로그 수집 및 저장
     * @param workId Work ID
     * @param dagId DAG ID (예: trend_pipeline)
     * @param runId Run ID (예: manual__2025-12-09T16:49:31.748347+00:00)
     */
    public void collectAndSaveLogs(Long workId, String dagId, String runId) {

        log.info("⚠️ Airflow 로그 수집 비활성화됨 - workId: {}, dagId: {}, runId: {}", workId, dagId, runId);
                log.info("Worker 로그를 통해 로그가 수집됩니다.");
        return;


//        log.info("로그 수집 시작 - workId: {}, dagId: {}, runId: {}", workId, dagId, runId);
//
//        List<WorkDetailLog> allLogs = new ArrayList<>();
//
//        // 로그 디렉토리 경로
//        String logDir = String.format("%s/dag_id=%s/run_id=%s",
//                airflowLogBasePath, dagId, runId);
//        Path logDirPath = Paths.get(logDir);
//
//        if (!Files.exists(logDirPath)) {
//            log.warn("로그 디렉토리가 존재하지 않음: {}", logDir);
//            return;
//        }
//
//        try (Stream<Path> taskDirs = Files.list(logDirPath)) {
//            taskDirs.filter(Files::isDirectory)
//                    .forEach(taskDir -> processTaskLogs(workId, taskDir, allLogs));
//        } catch (IOException e) {
//            log.error("로그 디렉토리 읽기 실패: {}", logDir, e);
//        }
//
//        // 배치 저장
//        saveLogsInBatch(allLogs);
//
//        log.info("로그 수집 완료 - workId: {}, 총 {}개 로그 저장", workId, allLogs.size());
//    }
//
//    /**
//     * Task별 로그 처리
//     */
//    private void processTaskLogs(Long workId, Path taskDir, List<WorkDetailLog> allLogs) {
//        String taskDirName = taskDir.getFileName().toString();
//
//        // task_id=select_keyword 형태에서 select_keyword 추출
//        if (!taskDirName.startsWith("task_id=")) {
//            return;
//        }
//
//        String taskId = taskDirName.substring("task_id=".length());
//        Integer stepNumber = TaskStepMapper.getStepNumber(taskId);
//        String stepName = TaskStepMapper.getStepName(taskId);
//
//        try (Stream<Path> logFiles = Files.list(taskDir)) {
//            logFiles.filter(file -> file.toString().endsWith(".log"))
//                    .forEach(logFile -> collectTaskLogs(workId, stepNumber, stepName, logFile, allLogs));
//        } catch (IOException e) {
//            log.error("Task 로그 파일 읽기 실패: {}", taskDir, e);
//        }
//    }
//
//    /**
//     * 개별 로그 파일 수집
//     */
//    private void collectTaskLogs(Long workId, Integer stepNumber, String stepName,
//                                  Path logFile, List<WorkDetailLog> allLogs) {
//        try {
//            String logContent = Files.readString(logFile);
//
//            // 사용자용 로그 파싱 (비즈니스 로직만)
//            List<ParsedLog> parsedLogs = logParser.parseForUser(logContent);
//
//            if (parsedLogs.isEmpty()) {
//                return;
//            }
//
//            // ✅ stepNumber별로 모든 로그를 하나로 결합 (개행문자로 구분)
//            String combinedLogs = parsedLogs.stream()
//                    .map(ParsedLog::getMessage)
//                    .collect(Collectors.joining("\n"));
//
//            // ✅ 대표 로그 레벨 결정 (가장 높은 레벨 선택)
//            LogLevel representativeLevel = parsedLogs.stream()
//                    .map(log -> mapLogLevel(log.getLogLevel()))
//                    .max((l1, l2) -> Integer.compare(l1.ordinal(), l2.ordinal()))
//                    .orElse(LogLevel.INFO);
//
//            // ✅ stepNumber당 하나의 WorkDetailLog만 생성
//            WorkDetailLog workDetailLog = WorkDetailLog.create(
//                    workId,
//                    stepNumber,
//                    stepName,
//                    combinedLogs,  // 모든 로그를 하나의 문자열로
//                    determineStatus(logContent),
//                    representativeLevel
//            );
//            allLogs.add(workDetailLog);
//
//            log.debug("로그 수집 - workId: {}, stepNumber: {}, 로그 라인 수: {}",
//                    workId, stepNumber, parsedLogs.size());
//
//        } catch (IOException e) {
//            log.error("로그 파일 읽기 실패: {}", logFile, e);
//        }
//    }
//
//    /**
//     * 로그 전체 내용으로 Step 상태 판단
//     */
//    private StepStatus determineStatus(String logContent) {
//        if (logContent.contains("Marking task as SUCCESS")) {
//            return StepStatus.SUCCESS;
//        } else if (logContent.contains("Marking task as FAILED") || logContent.contains("ERROR")) {
//            return StepStatus.FAILED;
//        } else if (logContent.contains("Marking task as SKIPPED")) {
//            return StepStatus.SKIPPED;
//        }
//        return StepStatus.SUCCESS; // 기본값
//    }
//
//    /**
//     * 로그 레벨 매핑
//     */
//    private LogLevel mapLogLevel(String level) {
//        return switch (level.toUpperCase()) {
//            case "TRACE" -> LogLevel.TRACE;
//            case "DEBUG" -> LogLevel.DEBUG;
//            case "INFO" -> LogLevel.INFO;
//            case "WARN", "WARNING" -> LogLevel.WARN;
//            case "ERROR" -> LogLevel.ERROR;
//            case "FATAL" -> LogLevel.FATAL;
//            default -> LogLevel.INFO;
//        };
//    }
//
//    /**
//     * 배치 로그 저장 (트랜잭션)
//     */
//    @Transactional
//    private void saveLogsInBatch(List<WorkDetailLog> logs) {
//        if (!logs.isEmpty()) {
//            logRepository.saveAll(logs);
//            log.debug("배치 저장 완료 - {}개 로그", logs.size());
//        }
    }
}

