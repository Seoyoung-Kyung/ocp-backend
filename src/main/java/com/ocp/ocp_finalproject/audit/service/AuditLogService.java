package com.ocp.ocp_finalproject.audit.service;

import com.ocp.ocp_finalproject.audit.domain.AuditLog;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import com.ocp.ocp_finalproject.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ActorType actorType,
                     Long actorId,
                     AuditAction action,
                     String targetType,
                     String targetId,
                     AuditResult result,
                     String failReason,
                     String ip,
                     String userAgent,
                     String requestId,
                     String extraJson) {
        try {
            AuditLog logEntity = AuditLog.createBuilder()
                    .actorType(actorType)
                    .actorId(actorId)
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .result(result)
                    .failReason(failReason)
                    .ip(ip)
                    .userAgent(userAgent)
                    .requestId(requestId)
                    .extraJson(extraJson)
                    .build();
            auditLogRepository.save(logEntity);
        } catch (Exception e) {
            log.warn("audit_log 저장 실패 action={} targetType={} targetId={} msg={}",
                    action, targetType, targetId, e.getMessage());
        }
    }
}
