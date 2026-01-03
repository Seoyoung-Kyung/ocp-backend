package com.ocp.ocp_finalproject.workflow.scheduler;

import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaleWorkflowCleanupScheduler {

    private final WorkflowRepository workflowRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteStaleTestWorkflows() {
        LocalDateTime threshold = LocalDateTime.now(ZoneOffset.UTC).minusHours(48);
        List<Workflow> staleWorkflows = workflowRepository.findStaleTestWorkflows(threshold);

        if (staleWorkflows.isEmpty()) {
            log.debug("삭제할 테스트 워크플로우 없음 (기준: {})", threshold);
            return;
        }

        log.info("테스트 워크플로우 정리 시작 - 대상 {}건 (기준: {})", staleWorkflows.size(), threshold);

        workflowRepository.deleteAll(staleWorkflows);

        log.info("테스트 워크플로우 정리 완료 - 삭제 {}건", staleWorkflows.size());
    }
}
