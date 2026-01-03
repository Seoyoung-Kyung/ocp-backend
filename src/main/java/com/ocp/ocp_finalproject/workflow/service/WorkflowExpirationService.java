package com.ocp.ocp_finalproject.workflow.service;

import com.ocp.ocp_finalproject.workflow.domain.RecurrenceRule;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExpirationService {

    private final WorkflowRepository workflowRepository;

    /**
     * 만료 시점이 지난 워크플로우를 완료 상태로 변경한다.
     *
     * @return true if completion applied
     */
    @Transactional
    public boolean completeIfExpired(Long workflowId) {
        Optional<Workflow> optionalWorkflow = workflowRepository.findByIdWithRecurrenceRule(workflowId);
        if (optionalWorkflow.isEmpty()) {
            log.warn("만료 처리 대상 워크플로우를 찾을 수 없습니다. id={}", workflowId);
            return false;
        }

        Workflow workflow = optionalWorkflow.get();
        RecurrenceRule rule = workflow.getRecurrenceRule();
        if (rule == null || rule.getEndAt() == null) {
            log.debug("워크플로우 {} 는 만료 시점이 없어 자동 완료를 건너뜁니다.", workflowId);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (rule.getEndAt().isAfter(now)) {
            log.debug("워크플로우 {} 의 만료 시점이 아직 지나지 않았습니다. endAt={}", workflowId, rule.getEndAt());
            return false;
        }

        if (workflow.getStatus().canTransitionTo(WorkflowStatus.COMPLETED)) {
            log.debug("워크플로우 {} 는 완료 상태로 전환할 수 없어 만료 처리를 건너뜁니다. currentStatus={}", workflowId, workflow.getStatus());
            return false;
        }

        workflow.changeStatus(WorkflowStatus.COMPLETED);
        log.info("워크플로우 {} 를 만료 시점 경과로 완료 처리했습니다.", workflowId);
        return true;
    }
}
